package com.sgn.shipping_app.service;

import com.sgn.shipping_app.entity.Customer;
import com.sgn.shipping_app.exception.ResourceNotFoundException;
import com.sgn.shipping_app.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Customer not found with ID: " + id));
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer updateCustomer(Long id, Customer updatedCustomer) {
        Customer existingCustomer = getCustomerById(id);

        existingCustomer.setName(updatedCustomer.getName());
        existingCustomer.setAddress(updatedCustomer.getAddress());
        existingCustomer.setPhone(updatedCustomer.getPhone());
        existingCustomer.setEmail(updatedCustomer.getEmail());

        return customerRepository.save(existingCustomer);
    }

    public void deleteCustomer(Long id) {
        Customer existingCustomer = getCustomerById(id);
        customerRepository.delete(existingCustomer);
    }
}
