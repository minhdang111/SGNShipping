package com.sgn.shipping_app.service;

import com.sgn.shipping_app.entity.*;
import com.sgn.shipping_app.exception.ResourceNotFoundException;
import com.sgn.shipping_app.repository.CustomerRepository;
import com.sgn.shipping_app.repository.RecipientRepository;
import com.sgn.shipping_app.repository.ShipmentRepository;
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
class ShipmentServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private RecipientRepository recipientRepository;

    @Mock
    private PricingService pricingService;

    @InjectMocks
    private ShipmentService shipmentService;

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

    private Shipment makeShipment(Long id, Customer customer, Recipient recipient) {
        Shipment s = new Shipment();
        s.setId(id);
        s.setCustomer(customer);
        s.setRecipient(recipient);
        s.setDescription("vitamins, clothes, 1 phone");
        s.setZone(DeliveryZone.CITY);
        s.setDeclaredValue(150.0);
        s.setStatus(ShipmentStatus.PENDING);
        s.setTotalCost(26.0);

        Box box = new Box();
        box.setLabel("A");
        box.setWeight(5.0);
        s.setBoxes(List.of(box));

        PackageItem item = new PackageItem();
        item.setItemName("Phone");
        item.setPricingType(PricingType.PER_EACH);
        item.setQuantity(1.0);
        item.setRate(10.0);
        s.setPackageItems(List.of(item));

        return s;
    }

    // ── createShipment ───────────────────────────────────────────────────────
    @Test
    void createShipment_validCustomerAndRecipient_returnsSavedShipment() {
        Customer customer = makeCustomer(1L);
        Recipient recipient = makeRecipient(1L, customer);

        // Incoming shipment has stub customer/recipient (just ids, like the mapper builds)
        Customer stubCustomer = new Customer();
        stubCustomer.setId(1L);
        Recipient stubRecipient = new Recipient();
        stubRecipient.setId(1L);

        Shipment input = makeShipment(null, stubCustomer, stubRecipient);
        Shipment saved = makeShipment(6362L, customer, recipient);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(recipientRepository.findById(1L)).thenReturn(Optional.of(recipient));
        when(shipmentRepository.save(any(Shipment.class))).thenReturn(saved);

        Shipment result = shipmentService.createShipment(input);

        assertEquals(6362L, result.getId());
        assertEquals("John Doe", result.getCustomer().getName());
        assertEquals("Jane Doe", result.getRecipient().getName());
        assertEquals(ShipmentStatus.PENDING, result.getStatus());

        // verify the full chain was called
        verify(customerRepository).findById(1L);
        verify(recipientRepository).findById(1L);
        verify(pricingService).calculateAndApplyPricing(any(Shipment.class));
        verify(shipmentRepository).save(any(Shipment.class));
    }

    @Test
    void createShipment_nonExistingCustomer_throwsResourceNotFoundException() {
        Customer stubCustomer = new Customer();
        stubCustomer.setId(9999L);
        Recipient stubRecipient = new Recipient();
        stubRecipient.setId(1L);

        Shipment input = makeShipment(null, stubCustomer, stubRecipient);

        // Customer does not exist
        when(customerRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> shipmentService.createShipment(input));

        verify(pricingService, never()).calculateAndApplyPricing(any());
        verify(shipmentRepository, never()).save(any());
    }

    @Test
    void createShipment_nonExistingRecipient_throwsResourceNotFoundException() {
        Customer customer = makeCustomer(1L);
        Customer stubCustomer = new Customer();
        stubCustomer.setId(1L);
        Recipient stubRecipient = new Recipient();
        stubRecipient.setId(9999L);

        Shipment input = makeShipment(null, stubCustomer, stubRecipient);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(recipientRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> shipmentService.createShipment(input));

        verify(pricingService, never()).calculateAndApplyPricing(any());
        verify(shipmentRepository, never()).save(any());
    }

    @Test
    void createShipment_boxesLinkedToShipment_beforeSave() {
        Customer customer = makeCustomer(1L);
        Recipient recipient = makeRecipient(1L, customer);

        Customer stubCustomer = new Customer();
        stubCustomer.setId(1L);
        Recipient stubRecipient = new Recipient();
        stubRecipient.setId(1L);

        Shipment input = makeShipment(null, stubCustomer, stubRecipient);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(recipientRepository.findById(1L)).thenReturn(Optional.of(recipient));
        when(shipmentRepository.save(any(Shipment.class))).thenAnswer(invocation -> {
            Shipment s = invocation.getArgument(0);
            // Verify all boxes and items have their shipment reference set
            s.getBoxes().forEach(box ->
                    assertNotNull(box.getShipment(),
                            "Box should have shipment reference set before save"));
            s.getPackageItems().forEach(item ->
                    assertNotNull(item.getShipment(),
                            "PackageItem should have shipment reference set before save"));
            return s;
        });

        shipmentService.createShipment(input);

        verify(shipmentRepository).save(any(Shipment.class));
    }

    // ── getShipmentById ──────────────────────────────────────────────────────
    @Test
    void getShipmentById_existingId_returnsShipment() {
        Customer customer = makeCustomer(1L);
        Recipient recipient = makeRecipient(1L, customer);
        Shipment shipment = makeShipment(6362L, customer, recipient);

        when(shipmentRepository.findById(6362L)).thenReturn(Optional.of(shipment));

        Shipment result = shipmentService.getShipmentById(6362L);

        assertEquals(6362L, result.getId());
        assertEquals("vitamins, clothes, 1 phone", result.getDescription());
    }

    @Test
    void getShipmentById_nonExistingId_throwsResourceNotFoundException() {
        when(shipmentRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> shipmentService.getShipmentById(9999L));
    }

    // ── getAllShipments ───────────────────────────────────────────────────────
    @Test
    void getAllShipments_returnsAllShipments() {
        Customer customer = makeCustomer(1L);
        Recipient recipient = makeRecipient(1L, customer);
        List<Shipment> shipments = List.of(
                makeShipment(6362L, customer, recipient),
                makeShipment(6363L, customer, recipient)
        );

        when(shipmentRepository.findAll()).thenReturn(shipments);

        List<Shipment> result = shipmentService.getAllShipments();

        assertEquals(2, result.size());
        verify(shipmentRepository).findAll();
    }

    // ── getShipmentsByCustomerPhone ───────────────────────────────────────────
    @Test
    void getShipmentsByCustomerPhone_matchingPhone_returnsShipments() {
        Customer customer = makeCustomer(1L);
        Recipient recipient = makeRecipient(1L, customer);
        List<Shipment> shipments = List.of(makeShipment(6362L, customer, recipient));

        when(shipmentRepository.findByCustomer_PhoneContaining("555"))
                .thenReturn(shipments);

        List<Shipment> result = shipmentService.getShipmentsByCustomerPhone("555");

        assertEquals(1, result.size());
        verify(shipmentRepository).findByCustomer_PhoneContaining("555");
    }

    // ── getShipmentsByStatus ──────────────────────────────────────────────────
    @Test
    void getShipmentsByStatus_pendingStatus_returnsMatchingShipments() {
        Customer customer = makeCustomer(1L);
        Recipient recipient = makeRecipient(1L, customer);
        List<Shipment> shipments = List.of(makeShipment(6362L, customer, recipient));

        when(shipmentRepository.findByStatus(ShipmentStatus.PENDING))
                .thenReturn(shipments);

        List<Shipment> result = shipmentService.getShipmentsByStatus(ShipmentStatus.PENDING);

        assertEquals(1, result.size());
        assertEquals(ShipmentStatus.PENDING, result.get(0).getStatus());
    }

    // ── updateTrackingNumber ──────────────────────────────────────────────────
    @Test
    void updateTrackingNumber_existingId_setsTrackingNumber() {
        Customer customer = makeCustomer(1L);
        Recipient recipient = makeRecipient(1L, customer);
        Shipment shipment = makeShipment(6362L, customer, recipient);

        when(shipmentRepository.findById(6362L)).thenReturn(Optional.of(shipment));
        when(shipmentRepository.save(any(Shipment.class))).thenReturn(shipment);

        Shipment result = shipmentService.updateTrackingNumber(6362L, "TRK123456");

        assertEquals("TRK123456", result.getTrackingNumber());
        verify(shipmentRepository).save(shipment);
    }

    @Test
    void updateTrackingNumber_nonExistingId_throwsResourceNotFoundException() {
        when(shipmentRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> shipmentService.updateTrackingNumber(9999L, "TRK123456"));
    }

    // ── markAsDelivered ───────────────────────────────────────────────────────
    @Test
    void markAsDelivered_existingId_setsStatusToDelivered() {
        Customer customer = makeCustomer(1L);
        Recipient recipient = makeRecipient(1L, customer);
        Shipment shipment = makeShipment(6362L, customer, recipient);

        when(shipmentRepository.findById(6362L)).thenReturn(Optional.of(shipment));
        when(shipmentRepository.save(any(Shipment.class))).thenReturn(shipment);

        Shipment result = shipmentService.markAsDelivered(6362L);

        assertEquals(ShipmentStatus.DELIVERED, result.getStatus());
        verify(shipmentRepository).save(shipment);
    }

    @Test
    void markAsDelivered_nonExistingId_throwsResourceNotFoundException() {
        when(shipmentRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> shipmentService.markAsDelivered(9999L));
    }

    // ── deleteShipment ────────────────────────────────────────────────────────
    @Test
    void deleteShipment_existingId_deletesShipment() {
        Customer customer = makeCustomer(1L);
        Recipient recipient = makeRecipient(1L, customer);
        Shipment shipment = makeShipment(6362L, customer, recipient);

        when(shipmentRepository.findById(6362L)).thenReturn(Optional.of(shipment));

        shipmentService.deleteShipment(6362L);

        verify(shipmentRepository).delete(shipment);
    }

    @Test
    void deleteShipment_nonExistingId_throwsResourceNotFoundException() {
        when(shipmentRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> shipmentService.deleteShipment(9999L));
    }
}
