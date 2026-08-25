package com.sgn.shipping_app.repository;

import com.sgn.shipping_app.entity.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipientRepository extends JpaRepository<Recipient, Long> {
    List<Recipient> findByCustomer_Id(Long customerId);
}