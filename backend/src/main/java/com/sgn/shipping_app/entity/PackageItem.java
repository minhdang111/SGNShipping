package com.sgn.shipping_app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "package_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackageItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Item name is required")
    @Column(nullable = false)
    private String itemName; // free text, staff-entered e.g. "iPhone", "Vitamins"

    @NotNull(message = "Pricing type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PricingType pricingType; // PER_POUND or PER_EACH

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    @Column(nullable = false)
    private Double quantity; // weight in lbs if PER_POUND, count if PER_EACH

    @NotNull(message = "Rate is required")
    @Positive(message = "Rate must be greater than zero")
    @Column(nullable = false)
    private Double rate; // $/lb if PER_POUND, $/item if PER_EACH

    // Always computed as quantity * rate, set by the service layer
    // before saving — never supplied directly by the client.
    private Double itemFee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Shipment shipment;
}