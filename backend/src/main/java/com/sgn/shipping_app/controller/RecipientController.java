package com.sgn.shipping_app.controller;

import com.sgn.shipping_app.dto.recipient.RecipientMapper;
import com.sgn.shipping_app.dto.recipient.RecipientRequest;
import com.sgn.shipping_app.dto.recipient.RecipientResponse;
import com.sgn.shipping_app.entity.Recipient;
import com.sgn.shipping_app.service.RecipientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipients")
@RequiredArgsConstructor
public class RecipientController {

    private final RecipientService recipientService;

    @PostMapping
    public ResponseEntity<RecipientResponse> createRecipient(
            @Valid @RequestBody RecipientRequest request) {
        Recipient saved = recipientService.createRecipient(
                RecipientMapper.toEntity(request));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(RecipientMapper.toResponse(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipientResponse> getRecipientById(
            @PathVariable Long id) {
        Recipient recipient = recipientService.getRecipientById(id);
        return ResponseEntity.ok(RecipientMapper.toResponse(recipient));
    }

    @GetMapping
    public ResponseEntity<List<RecipientResponse>> getAllRecipients() {
        List<RecipientResponse> responses = recipientService.getAllRecipients()
                .stream()
                .map(RecipientMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/by-customer/{customerId}")
    public ResponseEntity<List<RecipientResponse>> getRecipientsByCustomer(
            @PathVariable Long customerId) {
        List<RecipientResponse> responses = recipientService
                .getRecipientsByCustomer(customerId)
                .stream()
                .map(RecipientMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecipientResponse> updateRecipient(
            @PathVariable Long id,
            @Valid @RequestBody RecipientRequest request) {
        Recipient updated = recipientService.updateRecipient(id,
                RecipientMapper.toEntity(request));
        return ResponseEntity.ok(RecipientMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipient(@PathVariable Long id) {
        recipientService.deleteRecipient(id);
        return ResponseEntity.noContent().build();
    }
}
