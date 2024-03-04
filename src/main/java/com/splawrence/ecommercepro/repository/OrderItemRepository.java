package com.splawrence.ecommercepro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.splawrence.ecommercepro.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    public List<OrderItem> findByOrderId(@Param("id") Long id);
}
