package org.yascode.inventory_saas.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.yascode.inventory_saas.entities.StockMvt;

public interface StockMvtRepository extends JpaRepository<StockMvt, String> {

    Page<StockMvt> findAllByProductId(String productId, Pageable pageable);
}
