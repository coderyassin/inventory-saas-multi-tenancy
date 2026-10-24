package org.yascode.inventory_saas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yascode.inventory_saas.entities.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, String> {

    Optional<Category> findByNameIgnoreCase(String name);
}
