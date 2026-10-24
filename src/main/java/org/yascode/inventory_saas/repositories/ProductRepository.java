package org.yascode.inventory_saas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yascode.inventory_saas.entities.Product;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String> {

    Optional<Product> findByReferenceIgnoreCase(String reference);
}
