package com.sgn.shipping_app.repository;

import com.sgn.shipping_app.entity.Shipment;
import com.sgn.shipping_app.entity.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    List<Shipment> findByCustomer_PhoneContaining(String phone);
    List<Shipment> findByStatus(ShipmentStatus status);
    List<Shipment> findByRecipient_PhoneContaining(String phone);
}
