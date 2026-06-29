package com.sgn.shipping_app.service;

import com.sgn.shipping_app.entity.Customer;
import com.sgn.shipping_app.entity.Recipient;
import com.sgn.shipping_app.exception.ResourceNotFoundException;
import com.sgn.shipping_app.repository.CustomerRepository;
import com.sgn.shipping_app.repository.RecipientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipientService {

    private final RecipientRepository recipientRepository;
    private final CustomerRepository customerRepository;

    public Recipient createRecipient (Recipient recipient) {
        validateCustomerExistsIfProvided(recipient);
        return recipientRepository.save(recipient);
    }

    public Recipient getRecipientById(Long id) {
        return recipientRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Recipient not found with ID: " + id));
    }

    public List<Recipient> getAllRecipients() {
        return recipientRepository.findAll();
    }

    public List<Recipient> getRecipientsByCustomer(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with ID: " + customerId);
        }
        return recipientRepository.findByCustomer_Id(customerId);
    }

    public Recipient updateRecipient (Long id, Recipient updatedRecipient) {
        Recipient existingRecipient = getRecipientById(id);

        existingRecipient.setName(updatedRecipient.getName());
        existingRecipient.setAddress(updatedRecipient.getAddress());
        existingRecipient.setPhone(updatedRecipient.getPhone());

        return recipientRepository.save(existingRecipient);
    }

    public void deleteRecipient(Long id) {
        Recipient existingRecipient = getRecipientById(id);
        recipientRepository.delete(existingRecipient);
    }

    private void validateCustomerExistsIfProvided(Recipient recipient) {
        Customer customer = recipient.getCustomer();
        if (customer != null && customer.getId() != null) {
            customerRepository.findById(customer.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Customer not found with ID: " + customer.getId()));
        }
    }
}
