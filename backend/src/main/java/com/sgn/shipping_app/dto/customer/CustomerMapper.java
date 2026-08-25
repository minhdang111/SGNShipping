package com.sgn.shipping_app.dto.customer;

import com.sgn.shipping_app.entity.Customer;

public class CustomerMapper {

    // DTO coming IN from the frontend
    public static Customer toEntity(CustomerRequest request) {
        Customer customer = new Customer();
        customer.setName(request.name());
        customer.setAddress(request.address());
        customer.setPhone(request.phone());
        customer.setEmail(request.email());
        return customer;
    }

    // Entity coming OUT of the database
    public static CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getAddress(),
                customer.getPhone(),
                customer.getEmail()
        );
    }
}