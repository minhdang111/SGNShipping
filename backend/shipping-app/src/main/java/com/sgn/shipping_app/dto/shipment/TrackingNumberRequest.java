package com.sgn.shipping_app.dto.shipment;

import jakarta.validation.constraints.NotBlank;

public record TrackingNumberRequest(
        @NotBlank(message = "Tracking number is required")
        String trackingNumber
) {}
