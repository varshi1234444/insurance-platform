package com.ashith.insurance.claims_service.client;

import com.ashith.insurance.claims_service.dto.PolicySummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "policy-service")
public interface PolicyClient {

    @GetMapping("/api/policies/{id}")
    PolicySummary getPolicy(@PathVariable("id") Long id,
                            @RequestHeader("Authorization") String authorization);
}