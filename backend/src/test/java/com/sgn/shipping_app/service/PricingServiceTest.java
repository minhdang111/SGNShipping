package com.sgn.shipping_app.service;

import com.sgn.shipping_app.entity.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PricingServiceTest {

    private final PricingService pricingService = new PricingService();

    @Test
    void calculateAndApplyPricing_oneBoxNoItems_chargesWeightOnly() {
        Shipment shipment = new Shipment();
        shipment.setZone(DeliveryZone.CITY);

        Box box = new Box();
        box.setWeight(10.0);
        shipment.setBoxes(List.of(box));
        shipment.setPackageItems(List.of());

        pricingService.calculateAndApplyPricing(shipment);

        // 10 lbs * $4.5/lb (CITY rate) = $45
        assertEquals(45.0, shipment.getTotalCost(), 0.001);
    }

    @Test
    void calculateAndApplyPricing_multipleBoxes_sumsAllWeights() {
        Shipment shipment = new Shipment();
        shipment.setZone(DeliveryZone.CITY);

        Box boxA = new Box();
        boxA.setWeight(15.0);
        Box boxB = new Box();
        boxB.setWeight(35.0);

        shipment.setBoxes(List.of(boxA, boxB));
        shipment.setPackageItems(List.of());

        pricingService.calculateAndApplyPricing(shipment);

        // (15 + 35) lbs * $4.5/lb = $225
        assertEquals(225.0, shipment.getTotalCost(), 0.001);
    }

    @Test
    void calculateAndApplyPricing_otherZone_usesHigherRate() {
        Shipment shipment = new Shipment();
        shipment.setZone(DeliveryZone.OTHER);

        Box box = new Box();
        box.setWeight(15.0);
        shipment.setBoxes(List.of(box));
        shipment.setPackageItems(List.of());

        pricingService.calculateAndApplyPricing(shipment);

        // 15 lbs * $5/lb (OTHER rate) = $75
        assertEquals(75.0, shipment.getTotalCost(), 0.001);
    }

    @Test
    void calculateAndApplyPricing_perEachItem_calculatesFeeAndAddsToTotal() {
        Shipment shipment = new Shipment();
        shipment.setZone(DeliveryZone.CITY);

        Box box = new Box();
        box.setWeight(10.0);
        shipment.setBoxes(List.of(box));

        PackageItem phone = new PackageItem();
        phone.setPricingType(PricingType.PER_EACH);
        phone.setQuantity(2.0);
        phone.setRate(100.0);
        shipment.setPackageItems(List.of(phone));

        pricingService.calculateAndApplyPricing(shipment);

        // Item fee: 2 * $100 = $200, set directly on the item
        assertEquals(200.0, phone.getItemFee(), 0.001);

        // Total: $200 item fee + (10 lbs * $4.5/lb) weight charge = $245
        assertEquals(245.0, shipment.getTotalCost(), 0.001);
    }

    @Test
    void calculateAndApplyPricing_perPoundItem_calculatesFeeCorrectly() {
        Shipment shipment = new Shipment();
        shipment.setZone(DeliveryZone.CITY);

        Box box = new Box();
        box.setWeight(10.0);
        shipment.setBoxes(List.of(box));

        PackageItem vitamins = new PackageItem();
        vitamins.setPricingType(PricingType.PER_POUND);
        vitamins.setQuantity(4.0);
        vitamins.setRate(3.0);
        shipment.setPackageItems(List.of(vitamins));

        pricingService.calculateAndApplyPricing(shipment);

        // 4 lbs * $3/lb = $12
        assertEquals(12.0, vitamins.getItemFee(), 0.001);

        // $12 + 10 lbs * $4.5/lb = $57
        assertEquals(57.0, shipment.getTotalCost(), 0.001);
    }
}
