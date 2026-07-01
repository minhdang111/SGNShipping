package com.sgn.shipping_app.dto.customer;

public record CustomerResponse(
        Long id,
        String name,
        String address,
        String phone,
        String email
) {}