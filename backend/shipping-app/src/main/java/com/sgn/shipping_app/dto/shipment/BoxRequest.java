package com.sgn.shipping_app.dto.shipment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BoxRequest(
        String label, // null or A,B,C for multiple boxes in one shipment

        @NotNull(message = "Box weight is required")
        @Positive(message = "Weight must be greater than zero")
        Double weight
) {}
