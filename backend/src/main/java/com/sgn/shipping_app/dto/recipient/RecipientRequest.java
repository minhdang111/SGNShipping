package com.sgn.shipping_app.dto.recipient;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RecipientRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Address is required")
        String address,

        @NotBlank(message = "Phone number is required")
        String phone,

        @NotNull(message = "Customer ID is required")
        Long customerId
) {}
