package com.sgn.shipping_app.dto.shipment;

public record TodaySummaryResponse(
        long shipmentCount,
        double totalWeight
) {}
