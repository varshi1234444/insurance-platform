package com.ashith.insurance.loan_service.service;

import com.ashith.insurance.loan_service.client.CustomerClient;
import com.ashith.insurance.loan_service.dto.AccountSummary;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomerLookup {

    private final CustomerClient customerClient;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    public AccountSummary findPrimaryAccount(String customerEmail, String authorization) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("customerService");

        Optional<AccountSummary> result = circuitBreaker.run(
                () -> {
                    try {
                        return Optional.of(customerClient.primaryAccount(customerEmail, authorization));
                    } catch (FeignException.NotFound e) {
                        return Optional.empty();
                    }
                },
                throwable -> {
                    throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                            "Customer service is unavailable, try again later");
                });

        return result.orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT,
                "Customer has no primary bank account, cannot disburse"));
    }
}