package com.splawrence.ecommercepro.model;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import lombok.Data;

/**
 * Represents an item in a customer's order.
 */
@Entity
@Data
@Transactional
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "quantity", nullable = false)
    private int quantity;
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
    @Column(name = "updated", nullable = false)
    private LocalDateTime updated;
    
    // The order item is the owning side of the relationship
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    public Order order;

    // Straight forward relationship with Product
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    private Product product;
}
