package com.sgn.shipping_app.dto.shipment;

public record BoxResponse(
        Long id,
        String label,
        Double weight
) {}
