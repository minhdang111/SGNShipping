package com.sgn.shipping_app.dto.shipment;

import com.sgn.shipping_app.entity.DeliveryZone;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record ShipmentRequest(
        @NotNull(message = "Customer ID is required")
        Long customerId,

        @NotNull(message = "Recipient ID is required")
        Long recipientId,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Delivery zone is required")
        DeliveryZone zone,

        @PositiveOrZero(message = "Declared value cannot be negative")
        Double declaredValue,

        @NotEmpty(message = "At least one box is required")
        @Valid
        List<BoxRequest> boxes,

        @Valid
        List<PackageItemRequest> packageItems
) {}
