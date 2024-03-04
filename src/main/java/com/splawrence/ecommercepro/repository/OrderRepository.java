package com.splawrence.ecommercepro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.splawrence.ecommercepro.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{
    
}
