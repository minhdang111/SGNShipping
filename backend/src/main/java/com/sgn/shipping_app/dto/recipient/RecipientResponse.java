package com.sgn.shipping_app.dto.recipient;

public record RecipientResponse (
        Long id,
        String name,
        String address,
        String phone,
        Long customerId
) {}
