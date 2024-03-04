package com.splawrence.ecommercepro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.splawrence.ecommercepro.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{
}
