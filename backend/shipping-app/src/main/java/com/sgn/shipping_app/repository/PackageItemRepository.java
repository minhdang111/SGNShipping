package com.sgn.shipping_app.repository;

import com.sgn.shipping_app.entity.PackageItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageItemRepository extends JpaRepository<PackageItem, Long> {
}
