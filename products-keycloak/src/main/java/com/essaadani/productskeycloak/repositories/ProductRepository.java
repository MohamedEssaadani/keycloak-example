package com.essaadani.productskeycloak.repositories;

import com.essaadani.productskeycloak.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
