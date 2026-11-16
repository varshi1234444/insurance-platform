package com.ashith.insurance.loan_service.client;

import com.ashith.insurance.loan_service.dto.AccountSummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "customer-service")
public interface CustomerClient {

    @GetMapping("/api/customers/primary-account")
    AccountSummary primaryAccount(@RequestParam("email") String email,
                                  @RequestHeader("Authorization") String authorization);
}