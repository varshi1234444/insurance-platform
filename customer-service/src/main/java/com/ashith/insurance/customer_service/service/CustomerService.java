package com.ashith.insurance.customer_service.service;

import com.ashith.insurance.customer_service.dto.CustomerRequest;
import com.ashith.insurance.customer_service.dto.CustomerResponse;
import com.ashith.insurance.customer_service.dto.PageResponse;
import com.ashith.insurance.customer_service.entity.Customer;
import com.ashith.insurance.customer_service.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public CustomerResponse getByEmail(String email) {
        return CustomerResponse.from(findByEmailOrThrow(email));
    }

    @Transactional
    public CustomerResponse create(String email, CustomerRequest request) {
        if (customerRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Profile already exists");
        }
        Customer customer = Customer.builder()
                .email(email)
                .fullName(request.fullName())
                .phone(request.phone())
                .dateOfBirth(request.dateOfBirth())
                .address(request.address())
                .build();
        return CustomerResponse.from(customerRepository.save(customer));
    }

    @Transactional
    public CustomerResponse update(String email, CustomerRequest request) {
        Customer customer = findByEmailOrThrow(email);
        customer.setFullName(request.fullName());
        customer.setPhone(request.phone());
        customer.setDateOfBirth(request.dateOfBirth());
        customer.setAddress(request.address());
        return CustomerResponse.from(customerRepository.save(customer));
    }

    @Transactional(readOnly = true)
    public PageResponse<CustomerResponse> search(String name, Pageable pageable) {
        Page<Customer> page = customerRepository.searchByName(name, pageable);
        return new PageResponse<>(
                page.getContent().stream().map(CustomerResponse::from).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    private Customer findByEmailOrThrow(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer profile not found"));
    }
}