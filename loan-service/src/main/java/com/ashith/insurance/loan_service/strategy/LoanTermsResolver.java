package com.ashith.insurance.loan_service.strategy;

import com.ashith.insurance.loan_service.entity.LoanType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class LoanTermsResolver {

    private final Map<LoanType, LoanTermsStrategy> strategies = new EnumMap<>(LoanType.class);

    public LoanTermsResolver(List<LoanTermsStrategy> all) {
        all.forEach(s -> strategies.put(s.type(), s));
    }

    public LoanTermsStrategy resolve(LoanType type) {
        LoanTermsStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported loan type: " + type);
        }
        return strategy;
    }
}