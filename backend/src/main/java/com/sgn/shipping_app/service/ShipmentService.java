package com.sgn.shipping_app.service;

import com.sgn.shipping_app.dto.shipment.TodaySummaryResponse;
import com.sgn.shipping_app.entity.*;
import com.sgn.shipping_app.exception.ResourceNotFoundException;
import com.sgn.shipping_app.repository.CustomerRepository;
import com.sgn.shipping_app.repository.RecipientRepository;
import com.sgn.shipping_app.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final CustomerRepository customerRepository;
    private final RecipientRepository recipientRepository;
    private final PricingService pricingService;

    // Business operates out of Saint Paul, MN — use its local calendar day for
    // "today", not the server's ambient timezone (which is UTC in Docker).
    private static final ZoneId BUSINESS_ZONE = ZoneId.of("America/Chicago");

    public Shipment createShipment(Shipment shipment) {
        Customer customer = customerRepository.findById(shipment.getCustomer().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with ID: " + shipment.getCustomer().getId()));

        Recipient recipient = recipientRepository.findById(shipment.getRecipient().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Recipient not found with ID: " + shipment.getRecipient().getId()));

        shipment.setCustomer(customer);
        shipment.setRecipient(recipient);

        // Set foreign keys
        for (Box box : shipment.getBoxes()) {
            box.setShipment(shipment);
        }
        for (PackageItem item : shipment.getPackageItems()) {
            item.setShipment(shipment);
        }

        pricingService.calculateAndApplyPricing(shipment);

        return shipmentRepository.save(shipment);
    }

    public Shipment getShipmentById(Long id) {
        return shipmentRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Shipment not found with ID: " + id));
    }

    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    public List<Shipment> getShipmentsByCustomerPhone(String phone) {
        return shipmentRepository.findByCustomer_PhoneContaining(phone);
    }

    public List<Shipment> getShipmentsByStatus(ShipmentStatus status) {
        return shipmentRepository.findByStatus(status);
    }

    public List<Shipment> getShipmentsByDateRange(LocalDate startDate, LocalDate endDate) {
        return shipmentRepository.findByCreatedDateBetween(
                startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
    }

    public TodaySummaryResponse getTodaySummary() {
        LocalDate today = LocalDate.now(BUSINESS_ZONE);
        List<Shipment> todaysShipments = shipmentRepository.findByCreatedDateBetween(
                today.atStartOfDay(), today.atTime(LocalTime.MAX));

        double totalWeight = todaysShipments.stream()
                .flatMap(shipment -> shipment.getBoxes().stream())
                .mapToDouble(Box::getWeight)
                .sum();

        return new TodaySummaryResponse(todaysShipments.size(), totalWeight);
    }

    public Shipment updateTrackingNumber(Long id, String trackingNumber) {
        Shipment shipment = getShipmentById(id);
        shipment.setTrackingNumber(trackingNumber);
        return shipmentRepository.save(shipment);
    }

    public Shipment markAsDelivered(Long id) {
        Shipment shipment = getShipmentById(id);
        shipment.setStatus(ShipmentStatus.DELIVERED);
        return shipmentRepository.save(shipment);
    }

    public void deleteShipment(Long id) {
        Shipment shipment = getShipmentById(id);
        shipmentRepository.delete(shipment);
    }
}
