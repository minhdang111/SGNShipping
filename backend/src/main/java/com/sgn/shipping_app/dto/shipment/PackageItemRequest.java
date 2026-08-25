package com.sgn.shipping_app.dto.shipment;

import com.sgn.shipping_app.entity.PricingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PackageItemRequest(
        @NotBlank(message = "Item name is required")
        String itemName,

        @NotNull(message = "Pricing type is required")
        PricingType pricingType,

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be greater than zero")
        Double quantity,

        @NotNull(message = "Rate is required")
        @Positive(message = "Rate must be greater than zero")
        Double rate
) {}
