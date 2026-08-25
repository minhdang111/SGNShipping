package com.sgn.shipping_app.dto.shipment;

import com.sgn.shipping_app.entity.DeliveryZone;
import com.sgn.shipping_app.entity.ShipmentStatus;

import java.time.LocalDateTime;

public record ShipmentSummaryResponse(
        Long id,
        String recipientName,
        String description,
        DeliveryZone zone,
        ShipmentStatus status,
        Double totalCost,
        LocalDateTime createdDate
) {}
