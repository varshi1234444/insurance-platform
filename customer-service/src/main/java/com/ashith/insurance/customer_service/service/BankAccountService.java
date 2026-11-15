package com.ashith.insurance.customer_service.service;

import com.ashith.insurance.customer_service.dto.BankAccountRequest;
import com.ashith.insurance.customer_service.dto.BankAccountResponse;
import com.ashith.insurance.customer_service.entity.BankAccount;
import com.ashith.insurance.customer_service.entity.Customer;
import com.ashith.insurance.customer_service.repository.BankAccountRepository;
import com.ashith.insurance.customer_service.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HexFormat;
import java.util.List;

@Service
public class BankAccountService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;
    private final byte[] hashKey;

    public BankAccountService(CustomerRepository customerRepository,
                              BankAccountRepository bankAccountRepository,
                              @Value("${app.encryption-key}") String secret) {
        this.customerRepository = customerRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.hashKey = secret.getBytes(StandardCharsets.UTF_8);
    }

    @Transactional
    public BankAccountResponse add(String email, BankAccountRequest request) {
        Customer customer = findCustomer(email);
        String hash = hash(request.accountNumber());

        if (bankAccountRepository.existsByCustomerIdAndAccountNumberHash(customer.getId(), hash)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This bank account is already added");
        }

        boolean first = bankAccountRepository.countByCustomerId(customer.getId()) == 0;
        boolean makePrimary = first || Boolean.TRUE.equals(request.primaryAccount());
        if (makePrimary) {
            bankAccountRepository.clearPrimary(customer.getId());
        }

        String number = request.accountNumber();
        BankAccount saved = bankAccountRepository.save(BankAccount.builder()
                .customer(customer)
                .accountHolderName(request.accountHolderName())
                .bankName(request.bankName())
                .ifscCode(request.ifscCode())
                .accountNumber(number)
                .accountNumberLast4(number.substring(number.length() - 4))
                .accountNumberHash(hash)
                .primaryAccount(makePrimary)
                .build());
        return BankAccountResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<BankAccountResponse> list(String email) {
        Customer customer = findCustomer(email);
        return bankAccountRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId())
                .stream().map(BankAccountResponse::from).toList();
    }

    @Transactional
    public BankAccountResponse makePrimary(String email, Long accountId) {
        Customer customer = findCustomer(email);
        bankAccountRepository.clearPrimary(customer.getId());
        BankAccount account = findOwnedAccount(customer.getId(), accountId);
        account.setPrimaryAccount(true);
        return BankAccountResponse.from(bankAccountRepository.save(account));
    }

    @Transactional
    public void delete(String email, Long accountId) {
        Customer customer = findCustomer(email);
        BankAccount account = findOwnedAccount(customer.getId(), accountId);

        if (account.isPrimaryAccount() && bankAccountRepository.countByCustomerId(customer.getId()) > 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Make another account primary before deleting this one");
        }
        bankAccountRepository.delete(account);
    }

    private Customer findCustomer(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Create your profile first"));
    }

    private BankAccount findOwnedAccount(Long customerId, Long accountId) {
        return bankAccountRepository.findByIdAndCustomerId(accountId, customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bank account not found"));
    }

    private String hash(String accountNumber) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(hashKey, HMAC_ALGORITHM));
            return HexFormat.of().formatHex(mac.doFinal(accountNumber.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Hashing failed", e);
        }
    }
}