package com.ashith.insurance.customer_service.service;

import com.ashith.insurance.customer_service.dto.NomineeChangeCreateRequest;
import com.ashith.insurance.customer_service.dto.NomineeChangeResponse;
import com.ashith.insurance.customer_service.dto.PageResponse;
import com.ashith.insurance.customer_service.dto.ReviewRequest;
import com.ashith.insurance.customer_service.entity.Customer;
import com.ashith.insurance.customer_service.entity.NomineeChangeRequest;
import com.ashith.insurance.customer_service.entity.NomineeRequestStatus;
import com.ashith.insurance.customer_service.repository.CustomerRepository;
import com.ashith.insurance.customer_service.repository.NomineeChangeRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NomineeService {

    private final CustomerRepository customerRepository;
    private final NomineeChangeRequestRepository requestRepository;

    @Transactional
    public NomineeChangeResponse submit(String email, NomineeChangeCreateRequest request) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Create your profile first"));

        if (requestRepository.existsByCustomerIdAndStatus(customer.getId(), NomineeRequestStatus.PENDING)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You already have a pending nominee change request");
        }

        NomineeChangeRequest saved = requestRepository.save(NomineeChangeRequest.builder()
                .customer(customer)
                .newNomineeName(request.newNomineeName())
                .newNomineeRelationship(request.newNomineeRelationship())
                .reason(request.reason())
                .build());
        return NomineeChangeResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<NomineeChangeResponse> myRequests(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer profile not found"));
        return requestRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId())
                .stream().map(NomineeChangeResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<NomineeChangeResponse> pending(Pageable pageable) {
        Page<NomineeChangeRequest> page = requestRepository.findByStatus(NomineeRequestStatus.PENDING, pageable);
        return new PageResponse<>(
                page.getContent().stream().map(NomineeChangeResponse::from).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    @Transactional
    public NomineeChangeResponse review(Long id, String reviewerEmail, ReviewRequest review) {
        NomineeChangeRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));

        if (request.getStatus() != NomineeRequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Request has already been reviewed");
        }

        if (Boolean.TRUE.equals(review.approved())) {
            request.setStatus(NomineeRequestStatus.APPROVED);
            Customer customer = request.getCustomer();
            customer.setNomineeName(request.getNewNomineeName());
            customer.setNomineeRelationship(request.getNewNomineeRelationship());
        } else {
            request.setStatus(NomineeRequestStatus.REJECTED);
        }

        request.setReviewedBy(reviewerEmail);
        request.setReviewComment(review.comment());
        request.setReviewedAt(Instant.now());
        return NomineeChangeResponse.from(requestRepository.save(request));
    }
}
