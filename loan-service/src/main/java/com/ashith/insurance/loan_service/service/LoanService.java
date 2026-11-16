package com.ashith.insurance.loan_service.service;

import com.ashith.insurance.loan_service.dto.AccountSummary;
import com.ashith.insurance.loan_service.dto.LoanApplyRequest;
import com.ashith.insurance.loan_service.dto.LoanDecisionRequest;
import com.ashith.insurance.loan_service.dto.LoanResponse;
import com.ashith.insurance.loan_service.dto.PageResponse;
import com.ashith.insurance.loan_service.dto.PolicySummary;
import com.ashith.insurance.loan_service.entity.Loan;
import com.ashith.insurance.loan_service.entity.LoanStatus;
import com.ashith.insurance.loan_service.repository.LoanRepository;
import com.ashith.insurance.loan_service.strategy.LoanTermsResolver;
import com.ashith.insurance.loan_service.strategy.LoanTermsStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private static final List<LoanStatus> ACTIVE_STATUSES =
            List.of(LoanStatus.APPLIED, LoanStatus.APPROVED, LoanStatus.DISBURSED);

    private final LoanRepository loanRepository;
    private final PolicyLookup policyLookup;
    private final CustomerLookup customerLookup;
    private final LoanTermsResolver termsResolver;
    private final EmiCalculator emiCalculator;

    // No @Transactional: the remote policy call must not hold a database transaction open.
    public LoanResponse apply(String email, LoanApplyRequest request, String authorization) {
        PolicySummary policy = policyLookup.findPolicy(request.policyId(), authorization);
        if (!policy.active()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This policy is not active");
        }

        LoanTermsStrategy terms = termsResolver.resolve(request.loanType());

        if (request.tenureMonths() > terms.maxTenureMonths()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Maximum tenure for " + request.loanType() + " loans is " + terms.maxTenureMonths() + " months");
        }

        BigDecimal maxLoan = policy.coverAmount()
                .multiply(terms.maxPercentOfCover())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        if (request.principal().compareTo(maxLoan) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Maximum loan for this policy and loan type is " + maxLoan);
        }

        if (loanRepository.existsByPolicyIdAndStatusIn(policy.id(), ACTIVE_STATUSES)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This policy already has an active loan");
        }

        BigDecimal emi = emiCalculator.emi(request.principal(), terms.annualInterestRate(), request.tenureMonths());
        BigDecimal totalPayable = emi.multiply(BigDecimal.valueOf(request.tenureMonths()));

        Loan saved = loanRepository.save(Loan.builder()
                .customerEmail(email)
                .policyId(policy.id())
                .policyName(policy.name())
                .loanType(request.loanType())
                .principal(request.principal())
                .tenureMonths(request.tenureMonths())
                .annualInterestRate(terms.annualInterestRate())
                .emi(emi)
                .totalPayable(totalPayable)
                .build());
        return LoanResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> myLoans(String email) {
        return loanRepository.findByCustomerEmailOrderByCreatedAtDesc(email)
                .stream().map(LoanResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public LoanResponse getMine(String email, Long id) {
        Loan loan = loanRepository.findByIdAndCustomerEmail(id, email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found"));
        return LoanResponse.from(loan);
    }

    @Transactional(readOnly = true)
    public PageResponse<LoanResponse> byStatus(LoanStatus status, Pageable pageable) {
        Page<Loan> page = loanRepository.findByStatus(status, pageable);
        return new PageResponse<>(
                page.getContent().stream().map(LoanResponse::from).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    @Transactional
    public LoanResponse decide(Long id, String reviewerEmail, LoanDecisionRequest decision) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found"));

        if (loan.getStatus() != LoanStatus.APPLIED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only an applied loan can be reviewed");
        }

        if (Boolean.TRUE.equals(decision.approved())) {
            loan.setStatus(LoanStatus.APPROVED);
        } else {
            if (decision.comment() == null || decision.comment().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A comment is required when rejecting");
            }
            loan.setStatus(LoanStatus.REJECTED);
        }

        loan.setReviewedBy(reviewerEmail);
        loan.setReviewComment(decision.comment());
        return LoanResponse.from(loanRepository.save(loan));
    }

    // No @Transactional: the remote call to customer-service must not hold a database
    // transaction open. The final save is one short transaction, and @Version rejects a
    // second concurrent disbursement of the same loan.
    public LoanResponse disburse(Long id, String authorization) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found"));

        if (loan.getStatus() != LoanStatus.APPROVED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only an approved loan can be disbursed");
        }

        AccountSummary account = customerLookup.findPrimaryAccount(loan.getCustomerEmail(), authorization);

        loan.setStatus(LoanStatus.DISBURSED);
        loan.setDisbursedToAccount(account.bankName() + " " + account.maskedAccountNumber());
        loan.setDisbursedAt(Instant.now());
        return LoanResponse.from(loanRepository.save(loan));
    }
}