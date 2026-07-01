package com.sgn.shipping_app.service;

import com.sgn.shipping_app.entity.Box;
import com.sgn.shipping_app.entity.DeliveryZone;
import com.sgn.shipping_app.entity.PackageItem;
import com.sgn.shipping_app.entity.Shipment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PricingService {

    private static final double CITY_RATE_PER_LB = 4.50;
    private static final double OTHER_RATE_PER_LB = 5.00;

    public void calculateAndApplyPricing(Shipment shipment) {
        double itemFeesTotal = calculateItemFees(shipment.getPackageItems());
        double weightCharge = calculateWeightCharge(shipment.getBoxes(), shipment.getZone());

        shipment.setTotalCost(itemFeesTotal + weightCharge);
    }

    private double calculateItemFees(List<PackageItem> packageItems) {
        double total = 0.0;
        for (PackageItem item : packageItems) {
            double fee = item.getQuantity() * item.getRate();
            item.setItemFee(fee);
            total += fee;
        }

        return total;
    }

    private double calculateWeightCharge(List<Box> boxes, DeliveryZone zone) {
        double total = 0.0;
        double ratePerLbs = (zone == DeliveryZone.CITY) ? CITY_RATE_PER_LB : OTHER_RATE_PER_LB;
        for (Box box : boxes) {
            total += box.getWeight() * ratePerLbs;
        }

        return total;
    }
}
