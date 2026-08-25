package com.sgn.shipping_app.controller;

import com.sgn.shipping_app.dto.customer.CustomerMapper;
import com.sgn.shipping_app.dto.customer.CustomerRequest;
import com.sgn.shipping_app.dto.customer.CustomerResponse;
import com.sgn.shipping_app.entity.Customer;
import com.sgn.shipping_app.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(
            @Valid @RequestBody CustomerRequest request) {
        Customer saved = customerService.createCustomer(
                CustomerMapper.toEntity(request));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CustomerMapper.toResponse(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(
            @PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(CustomerMapper.toResponse(customer));
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        List<CustomerResponse> responses = customerService.getAllCustomers()
                .stream()
                .map(CustomerMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequest request) {
        Customer updated = customerService.updateCustomer(id,
                CustomerMapper.toEntity(request));
        return ResponseEntity.ok(CustomerMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
