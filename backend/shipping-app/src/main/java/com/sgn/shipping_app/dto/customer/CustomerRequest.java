package com.sgn.shipping_app.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Address is required")
        String address,

        @NotBlank(message = "Phone number is required")
        String phone,

        @Email(message = "Email must be valid")
        String email
) {}
