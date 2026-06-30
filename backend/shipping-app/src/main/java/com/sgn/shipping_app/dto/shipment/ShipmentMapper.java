package com.sgn.shipping_app.dto.shipment;

import com.sgn.shipping_app.dto.customer.CustomerMapper;
import com.sgn.shipping_app.dto.recipient.RecipientMapper;
import com.sgn.shipping_app.entity.*;

import java.util.List;

public class ShipmentMapper {

    // ShipmentRequest -> Shipment entity (used when creating a shipment)
    public static Shipment toEntity(ShipmentRequest request) {
        Shipment shipment = new Shipment();

        // Stub object, only to save customer_id foreign key in shipment, no save to DB
        Customer customer = new Customer();
        customer.setId(request.customerId());
        shipment.setCustomer(customer);

        // Stub object, only to save recipient_id foreign key in shipment , no save to DB
        Recipient recipient = new Recipient();
        recipient.setId(request.recipientId());
        shipment.setRecipient(recipient);

        shipment.setDescription(request.description());
        shipment.setZone(request.zone());
        shipment.setDeclaredValue(request.declaredValue());

        List<Box> boxes = request.boxes().stream()
                .map(ShipmentMapper::toBoxEntity)
                .toList();
        shipment.setBoxes(boxes);

        List<PackageItem> items = (request.packageItems() == null)
                ? List.of()
                : request.packageItems().stream()
                        .map(ShipmentMapper::toPackageItemEntity)
                        .toList();
        shipment.setPackageItems(items);

        return shipment;
    }

    private static Box toBoxEntity(BoxRequest request) {
        Box box = new Box();
        box.setLabel(request.label());
        box.setWeight(request.weight());
        return box;
    }

    private static PackageItem toPackageItemEntity(PackageItemRequest request) {
        PackageItem item = new PackageItem();
        item.setItemName(request.itemName());
        item.setPricingType(request.pricingType());
        item.setQuantity(request.quantity());
        item.setRate(request.rate());
        return item;
    }

    // Shipment entity -> full ShipmentResponse (detailed view, e.g. for printing)
    public static ShipmentResponse toResponse(Shipment shipment) {
        List<BoxResponse> boxResponses = shipment.getBoxes().stream()
                .map(ShipmentMapper::toBoxResponse)
                .toList();

        List<PackageItemResponse> itemResponses = shipment.getPackageItems().stream()
                .map(ShipmentMapper::toPackageItemResponse)
                .toList();

        return new ShipmentResponse(
                shipment.getId(),
                CustomerMapper.toResponse(shipment.getCustomer()),
                RecipientMapper.toResponse(shipment.getRecipient()),
                shipment.getDescription(),
                shipment.getTrackingNumber(),
                shipment.getZone(),
                shipment.getStatus(),
                shipment.getDeclaredValue(),
                shipment.getTotalCost(),
                shipment.getCreatedDate(),
                boxResponses,
                itemResponses
        );
    }

    private static BoxResponse toBoxResponse(Box box) {
        return new BoxResponse(box.getId(), box.getLabel(), box.getWeight());
    }

    private static PackageItemResponse toPackageItemResponse(PackageItem item) {
        return new PackageItemResponse(
                item.getId(),
                item.getItemName(),
                item.getPricingType(),
                item.getQuantity(),
                item.getRate(),
                item.getItemFee()
        );
    }

    // Shipment entity -> lightweight ShipmentSummaryResponse (e.g. shipment history list)
    public static ShipmentSummaryResponse toSummaryResponse(Shipment shipment) {
        return new ShipmentSummaryResponse(
                shipment.getId(),
                shipment.getRecipient().getName(),
                shipment.getDescription(),
                shipment.getZone(),
                shipment.getStatus(),
                shipment.getTotalCost(),
                shipment.getCreatedDate()
        );
    }
}