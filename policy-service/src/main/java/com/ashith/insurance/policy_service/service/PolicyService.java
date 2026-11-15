package com.ashith.insurance.policy_service.service;

import com.ashith.insurance.policy_service.dto.PageResponse;
import com.ashith.insurance.policy_service.dto.PolicyRequest;
import com.ashith.insurance.policy_service.dto.PolicyResponse;
import com.ashith.insurance.policy_service.entity.Policy;
import com.ashith.insurance.policy_service.entity.PolicyType;
import com.ashith.insurance.policy_service.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyRepository policyRepository;

    @Transactional
    public PolicyResponse create(PolicyRequest request) {
        if (policyRepository.existsByNameIgnoreCase(request.name())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A policy with this name already exists");
        }
        Policy saved = policyRepository.save(Policy.builder()
                .name(request.name())
                .type(request.type())
                .description(request.description())
                .annualPremium(request.annualPremium())
                .coverAmount(request.coverAmount())
                .termYears(request.termYears())
                .build());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<PolicyResponse> list(PolicyType type, Pageable pageable) {
        Page<Policy> page = (type == null)
                ? policyRepository.findByActiveTrue(pageable)
                : policyRepository.findByTypeAndActiveTrue(type, pageable);
        return new PageResponse<>(
                page.getContent().stream().map(this::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    @Transactional(readOnly = true)
    public PolicyResponse get(Long id) {
        return toResponse(findActive(id));
    }

    @Transactional
    public PolicyResponse update(Long id, PolicyRequest request) {
        Policy policy = findActive(id);
        boolean nameChanged = !policy.getName().equalsIgnoreCase(request.name());
        if (nameChanged && policyRepository.existsByNameIgnoreCase(request.name())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A policy with this name already exists");
        }
        policy.setName(request.name());
        policy.setType(request.type());
        policy.setDescription(request.description());
        policy.setAnnualPremium(request.annualPremium());
        policy.setCoverAmount(request.coverAmount());
        policy.setTermYears(request.termYears());
        return toResponse(policyRepository.save(policy));
    }

    @Transactional
    public void deactivate(Long id) {
        Policy policy = findActive(id);
        policy.setActive(false);
        policyRepository.save(policy);
    }

    private Policy findActive(Long id) {
        return policyRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy not found"));
    }

    private PolicyResponse toResponse(Policy p) {
        return new PolicyResponse(p.getId(), p.getName(), p.getType(), p.getDescription(),
                p.getAnnualPremium(), p.getCoverAmount(), p.getTermYears(), p.isActive());
    }
}