package com.ashith.insurance.claims_service.service;

import com.ashith.insurance.claims_service.dto.ClaimRequest;
import com.ashith.insurance.claims_service.dto.ClaimResponse;
import com.ashith.insurance.claims_service.dto.PageResponse;
import com.ashith.insurance.claims_service.dto.PolicySummary;
import com.ashith.insurance.claims_service.dto.ReviewClaimRequest;
import com.ashith.insurance.claims_service.entity.Claim;
import com.ashith.insurance.claims_service.entity.ClaimStatus;
import com.ashith.insurance.claims_service.repository.ClaimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final PolicyLookup policyLookup;

    // No @Transactional here on purpose: the remote call to policy-service must not
    // hold a database transaction open. repository.save() runs in its own short transaction.
    public ClaimResponse submit(String email, ClaimRequest request, String authorization) {
        PolicySummary policy = policyLookup.findPolicy(request.policyId(), authorization);
        if (!policy.active()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This policy is not active");
        }
        if (request.claimAmount().compareTo(policy.coverAmount()) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Claim amount exceeds the policy cover amount");
        }

        Claim saved = claimRepository.save(Claim.builder()
                .customerEmail(email)
                .policyId(policy.id())
                .policyName(policy.name())
                .claimAmount(request.claimAmount())
                .incidentDate(request.incidentDate())
                .description(request.description())
                .build());
        return ClaimResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<ClaimResponse> myClaims(String email) {
        return claimRepository.findByCustomerEmailOrderByCreatedAtDesc(email)
                .stream().map(ClaimResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ClaimResponse getMine(String email, Long id) {
        Claim claim = claimRepository.findByIdAndCustomerEmail(id, email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim not found"));
        return ClaimResponse.from(claim);
    }

    @Transactional(readOnly = true)
    public PageResponse<ClaimResponse> byStatus(ClaimStatus status, Pageable pageable) {
        Page<Claim> page = claimRepository.findByStatus(status, pageable);
        return new PageResponse<>(
                page.getContent().stream().map(ClaimResponse::from).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    @Transactional
    public ClaimResponse startReview(Long id, String officerEmail) {
        Claim claim = findClaim(id);
        if (claim.getStatus() != ClaimStatus.SUBMITTED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only a submitted claim can be taken for review");
        }
        claim.setStatus(ClaimStatus.UNDER_REVIEW);
        claim.setReviewedBy(officerEmail);
        return ClaimResponse.from(claimRepository.save(claim));
    }

    @Transactional
    public ClaimResponse decide(Long id, String officerEmail, ReviewClaimRequest review) {
        Claim claim = findClaim(id);
        if (claim.getStatus() != ClaimStatus.UNDER_REVIEW) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The claim must be under review");
        }

        if (Boolean.TRUE.equals(review.approved())) {
            BigDecimal amount = review.approvedAmount() != null ? review.approvedAmount() : claim.getClaimAmount();
            if (amount.compareTo(claim.getClaimAmount()) > 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Approved amount cannot exceed the claimed amount");
            }
            claim.setApprovedAmount(amount);
            claim.setStatus(ClaimStatus.APPROVED);
        } else {
            if (review.comment() == null || review.comment().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A comment is required when rejecting");
            }
            claim.setStatus(ClaimStatus.REJECTED);
        }

        claim.setReviewedBy(officerEmail);
        claim.setReviewComment(review.comment());
        return ClaimResponse.from(claimRepository.save(claim));
    }

    private Claim findClaim(Long id) {
        return claimRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim not found"));
    }
}