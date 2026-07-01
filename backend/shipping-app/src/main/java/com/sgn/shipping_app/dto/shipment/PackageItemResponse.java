package com.sgn.shipping_app.dto.shipment;

import com.sgn.shipping_app.entity.PricingType;

public record PackageItemResponse(
        Long id,
        String itemName,
        PricingType pricingType,
        Double quantity,
        Double rate,
        Double itemFee
) {}
