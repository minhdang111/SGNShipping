package com.sgn.shipping_app.dto.recipient;

import com.sgn.shipping_app.entity.Customer;
import com.sgn.shipping_app.entity.Recipient;

public class RecipientMapper {

    public static Recipient toEntity(RecipientRequest request) {
        Recipient recipient = new Recipient();
        recipient.setName(request.name());
        recipient.setAddress(request.address());
        recipient.setPhone(request.phone());

        // Stub object, only to save to CustomerId, no save to DB
        Customer customer = new Customer();
        customer.setId(request.customerId());
        recipient.setCustomer(customer);

        return recipient;
    }

    public static RecipientResponse toResponse(Recipient recipient) {
        return new RecipientResponse(
                recipient.getId(),
                recipient.getName(),
                recipient.getAddress(),
                recipient.getPhone(),
                recipient.getCustomer().getId()
        );
    }
}
