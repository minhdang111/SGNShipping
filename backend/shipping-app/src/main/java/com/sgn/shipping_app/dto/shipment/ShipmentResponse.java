package com.sgn.shipping_app.dto.shipment;

import com.sgn.shipping_app.dto.customer.CustomerResponse;
import com.sgn.shipping_app.dto.recipient.RecipientResponse;
import com.sgn.shipping_app.entity.DeliveryZone;
import com.sgn.shipping_app.entity.ShipmentStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ShipmentResponse(
        Long id,
        CustomerResponse customer,
        RecipientResponse recipient,
        String description,
        String trackingNumber,
        DeliveryZone zone,
        ShipmentStatus status,
        Double declaredValue,
        Double totalCost,
        LocalDateTime createdDate,
        List<BoxResponse> boxes,
        List<PackageItemResponse> packageItems
) {}
