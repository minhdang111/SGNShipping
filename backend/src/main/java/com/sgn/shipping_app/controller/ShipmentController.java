package com.sgn.shipping_app.controller;

import com.sgn.shipping_app.dto.shipment.*;
import com.sgn.shipping_app.entity.Shipment;
import com.sgn.shipping_app.entity.ShipmentStatus;
import com.sgn.shipping_app.service.ShipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping
    public ResponseEntity<ShipmentResponse> createShipment(
            @Valid @RequestBody ShipmentRequest request) {
        Shipment saved = shipmentService.createShipment(
                ShipmentMapper.toEntity(request));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ShipmentMapper.toResponse(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipmentResponse> getShipmentById(
            @PathVariable Long id) {
        Shipment shipment = shipmentService.getShipmentById(id);
        return ResponseEntity.ok(ShipmentMapper.toResponse(shipment));
    }

    @GetMapping
    public ResponseEntity<List<ShipmentSummaryResponse>> getAllShipments() {
        List<ShipmentSummaryResponse> responses = shipmentService.getAllShipments()
                .stream()
                .map(ShipmentMapper::toSummaryResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/by-customer-phone")
    public ResponseEntity<List<ShipmentSummaryResponse>> getShipmentsByCustomerPhone(
            @RequestParam String phone) {
        List<ShipmentSummaryResponse> responses = shipmentService
                .getShipmentsByCustomerPhone(phone)
                .stream()
                .map(ShipmentMapper::toSummaryResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<ShipmentSummaryResponse>> getShipmentsByStatus(
            @PathVariable ShipmentStatus status) {
        List<ShipmentSummaryResponse> responses = shipmentService
                .getShipmentsByStatus(status)
                .stream()
                .map(ShipmentMapper::toSummaryResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/by-date-range")
    public ResponseEntity<List<ShipmentSummaryResponse>> getShipmentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        List<ShipmentSummaryResponse> responses = shipmentService
                .getShipmentsByDateRange(start, end)
                .stream()
                .map(ShipmentMapper::toSummaryResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{id}/tracking-number")
    public ResponseEntity<ShipmentResponse> updateTrackingNumber(
            @PathVariable Long id,
            @Valid @RequestBody TrackingNumberRequest request) {
        Shipment updated = shipmentService.updateTrackingNumber(
                id, request.trackingNumber());
        return ResponseEntity.ok(ShipmentMapper.toResponse(updated));
    }

    @PatchMapping("/{id}/mark-delivered")
    public ResponseEntity<ShipmentResponse> markAsDelivered(
            @PathVariable Long id) {
        Shipment updated = shipmentService.markAsDelivered(id);
        return ResponseEntity.ok(ShipmentMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShipment(@PathVariable Long id) {
        shipmentService.deleteShipment(id);
        return ResponseEntity.noContent().build();
    }
}
