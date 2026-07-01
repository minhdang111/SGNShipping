package com.sgn.shipping_app.service;

import com.sgn.shipping_app.entity.Customer;
import com.sgn.shipping_app.entity.Recipient;
import com.sgn.shipping_app.exception.ResourceNotFoundException;
import com.sgn.shipping_app.repository.CustomerRepository;
import com.sgn.shipping_app.repository.RecipientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipientServiceTest {

    @Mock
    private RecipientRepository recipientRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private RecipientService recipientService;

    // ── helpers ──────────────────────────────────────────────────────────────
    private Customer makeCustomer(Long id) {
        Customer c = new Customer();
        c.setId(id);
        c.setName("John Doe");
        c.setPhone("555-1234");
        c.setAddress("123 Main St");
        return c;
    }

    private Recipient makeRecipient(Long id, Customer customer) {
        Recipient r = new Recipient();
        r.setId(id);
        r.setName("Jane Doe");
        r.setAddress("456 Other St");
        r.setPhone("555-5678");
        r.setCustomer(customer);
        return r;
    }

    // ── createRecipient ──────────────────────────────────────────────────────
    @Test
    void createRecipient_validCustomer_returnsSavedRecipient() {
        Customer customer = makeCustomer(1L);
        Recipient input = makeRecipient(null, customer);
        Recipient saved = makeRecipient(1L, customer);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(recipientRepository.save(input)).thenReturn(saved);

        Recipient result = recipientService.createRecipient(input);

        assertEquals(1L, result.getId());
        assertEquals("Jane Doe", result.getName());
        assertEquals("555-5678", result.getPhone());
        assertEquals("456 Other St", result.getAddress());
        assertEquals(customer, result.getCustomer());

        verify(customerRepository).findById(1L);
        verify(recipientRepository).save(input);
    }

    @Test
    void createRecipient_nonExistingCustomer_throwsResourceNotFoundException() {
        Customer stubCustomer = new Customer();
        stubCustomer.setId(9999L);
        Recipient input = makeRecipient(null, stubCustomer);

        when(customerRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> recipientService.createRecipient(input));

        verify(recipientRepository, never()).save(any());
    }

    // ── getRecipientById ─────────────────────────────────────────────────────
    @Test
    void getRecipientById_existingId_returnsRecipient() {
        Customer customer = makeCustomer(1L);
        Recipient recipient = makeRecipient(1L, customer);

        when(recipientRepository.findById(1L)).thenReturn(Optional.of(recipient));

        Recipient result = recipientService.getRecipientById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Jane Doe", result.getName());
        assertEquals("555-5678", result.getPhone());
        assertEquals("456 Other St", result.getAddress());
        assertEquals(customer, result.getCustomer());
    }

    @Test
    void getRecipientById_nonExistingId_throwsResourceNotFoundException() {
        when(recipientRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> recipientService.getRecipientById(9999L));
    }

    // ── getAllRecipients ──────────────────────────────────────────────────────
    @Test
    void getAllRecipients_returnsAllRecipients() {
        Customer customer = makeCustomer(1L);
        List<Recipient> recipients = List.of(
                makeRecipient(1L, customer),
                makeRecipient(2L, customer)
        );

        when(recipientRepository.findAll()).thenReturn(recipients);

        List<Recipient> result = recipientService.getAllRecipients();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Jane Doe", result.get(0).getName());
        assertEquals("555-5678", result.get(0).getPhone());
        assertEquals("456 Other St", result.get(0).getAddress());
        assertEquals(customer, result.get(0).getCustomer());

        assertEquals(2L, result.get(1).getId());

        verify(recipientRepository).findAll();
    }

    // ── getRecipientsByCustomer ───────────────────────────────────────────────
    @Test
    void getRecipientsByCustomer_existingCustomer_returnsRecipients() {
        Customer customer = makeCustomer(1L);
        List<Recipient> recipients = List.of(
                makeRecipient(1L, customer),
                makeRecipient(2L, customer)
        );

        when(customerRepository.existsById(1L)).thenReturn(true);
        when(recipientRepository.findByCustomer_Id(1L)).thenReturn(recipients);

        List<Recipient> result = recipientService.getRecipientsByCustomer(1L);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Jane Doe", result.get(0).getName());
        assertEquals("555-5678", result.get(0).getPhone());
        assertEquals("456 Other St", result.get(0).getAddress());
        assertEquals(customer, result.get(0).getCustomer());

        assertEquals(2L, result.get(1).getId());

        verify(customerRepository).existsById(1L);
        verify(recipientRepository).findByCustomer_Id(1L);
    }

    @Test
    void getRecipientsByCustomer_nonExistingCustomer_throwsResourceNotFoundException() {
        when(customerRepository.existsById(9999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> recipientService.getRecipientsByCustomer(9999L));

        verify(recipientRepository, never()).findByCustomer_Id(any());
    }

    // ── updateRecipient ───────────────────────────────────────────────────────
    @Test
    void updateRecipient_existingId_updatesAndReturnsRecipient() {
        Customer customer = makeCustomer(1L);
        Recipient existing = makeRecipient(1L, customer);

        Recipient updates = new Recipient();
        updates.setName("Updated Name");
        updates.setAddress("Updated Address");
        updates.setPhone("555-0000");

        when(recipientRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(recipientRepository.save(any(Recipient.class))).thenReturn(existing);

        Recipient result = recipientService.updateRecipient(1L, updates);

        assertEquals("Updated Name", result.getName());
        assertEquals("Updated Address", result.getAddress());
        assertEquals("555-0000", result.getPhone());
        // customer link should NOT have changed
        assertEquals(customer, result.getCustomer());
        verify(recipientRepository).save(existing);
    }

    @Test
    void updateRecipient_nonExistingId_throwsResourceNotFoundException() {
        when(recipientRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> recipientService.updateRecipient(9999L, makeRecipient(null, null)));
    }

    // ── deleteRecipient ───────────────────────────────────────────────────────
    @Test
    void deleteRecipient_existingId_deletesRecipient() {
        Customer customer = makeCustomer(1L);
        Recipient recipient = makeRecipient(1L, customer);

        when(recipientRepository.findById(1L)).thenReturn(Optional.of(recipient));

        recipientService.deleteRecipient(1L);

        verify(recipientRepository).delete(recipient);
    }

    @Test
    void deleteRecipient_nonExistingId_throwsResourceNotFoundException() {
        when(recipientRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> recipientService.deleteRecipient(9999L));
    }
}
