package com.ashith.insurance.loan_service.service;

import com.ashith.insurance.loan_service.client.PolicyClient;
import com.ashith.insurance.loan_service.dto.PolicySummary;
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
public class PolicyLookup {

    private final PolicyClient policyClient;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    public PolicySummary findPolicy(Long policyId, String authorization) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("policyService");

        Optional<PolicySummary> result = circuitBreaker.run(
                () -> {
                    try {
                        return Optional.of(policyClient.getPolicy(policyId, authorization));
                    } catch (FeignException.NotFound e) {
                        // A missing policy is a normal answer, not a failure, so return it
                        // as a value and the circuit breaker does not count it.
                        return Optional.empty();
                    }
                },
                throwable -> {
                    throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                            "Policy service is unavailable, try again later");
                });

        return result.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy not found"));
    }
}