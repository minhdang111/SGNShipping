package com.sgn.shipping_app.service;

import com.sgn.shipping_app.entity.Customer;
import com.sgn.shipping_app.exception.ResourceNotFoundException;
import com.sgn.shipping_app.repository.CustomerRepository;
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
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    // ── helpers ──────────────────────────────────────────────────────────────
    private Customer makeCustomer(Long id) {
        Customer c = new Customer();
        c.setId(id);
        c.setName("John Doe");
        c.setPhone("555-1234");
        c.setAddress("123 Main St");
        c.setEmail(null);
        return c;
    }
    // ── createCustomer ──────────────────────────────────────────────────────
    @Test
    void createCustomer_validCustomer_returnsSavedCustomer() {
        Customer input = makeCustomer(null); // no id yet — not saved yet
        Customer saved = makeCustomer(1L);   // id assigned after save

        when(customerRepository.save(input)).thenReturn(saved);

        Customer result = customerService.createCustomer(input);

        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("555-1234", result.getPhone());
        assertEquals("123 Main St", result.getAddress());
        assertNull(result.getEmail());
        verify(customerRepository).save(input); // confirm save was actually called
    }

    // ── getCustomerById ──────────────────────────────────────────────────────
    @Test
    void getCustomerById_existingId_returnsCustomer() {
        Customer customer = makeCustomer(1L);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Customer result = customerService.getCustomerById(1L);

        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("555-1234", result.getPhone());
        assertEquals("123 Main St", result.getAddress());
        assertNull(result.getEmail());
    }

    @Test
    void getCustomerById_nonExistingId_throwsResourceNotFoundException() {
        when(customerRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> customerService.getCustomerById(9999L));
    }

    // ── getAllCustomers ───────────────────────────────────────────────────────
    @Test
    void getAllCustomers_returnsAllCustomers() {
        List<Customer> customers = List.of(makeCustomer(1L), makeCustomer(2L));
        when(customerRepository.findAll()).thenReturn(customers);

        List<Customer> result = customerService.getAllCustomers();

        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).getId());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("555-1234", result.get(0).getPhone());
        assertEquals("123 Main St", result.get(0).getAddress());
        assertNull(result.get(0).getEmail());

        assertEquals(2L, result.get(1).getId());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("555-1234", result.get(0).getPhone());
        assertEquals("123 Main St", result.get(0).getAddress());
        assertNull(result.get(0).getEmail());

        verify(customerRepository).findAll();
    }

    // ── getCustomerByPhone ────────────────────────────────────────────────────
    @Test
    void getCustomerByPhone_matchingPhone_returnsCustomers() {
        List<Customer> customers = List.of(makeCustomer(1L));
        when(customerRepository.findByPhone("5551234")).thenReturn(customers);

        List<Customer> result = customerService.getCustomerByPhone("5551234");

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
        verify(customerRepository).findByPhone("5551234");
    }

    @Test
    void getCustomerByPhone_noMatch_returnsEmptyList() {
        when(customerRepository.findByPhone("0000000000")).thenReturn(List.of());

        List<Customer> result = customerService.getCustomerByPhone("0000000000");

        assertTrue(result.isEmpty());
    }

    // ── updateCustomer ───────────────────────────────────────────────────────
    @Test
    void updateCustomer_existingId_updatesAndReturnsCustomer() {
        Customer existing = makeCustomer(1L);

        Customer updates = new Customer();
        updates.setName("Jane Doe");
        updates.setPhone("555-9999");
        updates.setAddress("456 Other St");
        updates.setEmail("jane@example.com");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
        // updateCustomer mutates existing, so by the time save is called
        // existing has already changed to update information
        when(customerRepository.save(any(Customer.class))).thenReturn(existing);

        Customer result = customerService.updateCustomer(1L, updates);

        assertEquals("Jane Doe", result.getName());
        assertEquals("555-9999", result.getPhone());
        assertEquals("456 Other St", result.getAddress());
        assertEquals("jane@example.com", result.getEmail());
        verify(customerRepository).save(existing);
    }

    @Test
    void updateCustomer_nonExistingId_throwsResourceNotFoundException() {
        when(customerRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> customerService.updateCustomer(9999L, makeCustomer(null)));
    }

    // ── deleteCustomer ───────────────────────────────────────────────────────
    @Test
    void deleteCustomer_existingId_deletesCustomer() {
        Customer customer = makeCustomer(1L);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        customerService.deleteCustomer(1L);

        verify(customerRepository).delete(customer);
    }

    @Test
    void deleteCustomer_nonExistingId_throwsResourceNotFoundException() {
        when(customerRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> customerService.deleteCustomer(9999L));
    }
}