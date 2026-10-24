package org.yascode.inventory_saas.mappers;

import org.springframework.stereotype.Component;
import org.yascode.inventory_saas.entities.Product;
import org.yascode.inventory_saas.entities.StockMvt;
import org.yascode.inventory_saas.requests.StockMvtRequest;
import org.yascode.inventory_saas.responses.StockMvtResponse;

@Component
public class StockMvtMapper {

    public StockMvt toEntity(final StockMvtRequest request) {
        return StockMvt.builder()
                       .dateMvt(request.getDateMvt())
                       .comment(request.getComment())
                       .typeMvt(request.getTypeMvt())
                       .quantity(request.getQuantity())
                       .product(Product.builder()
                                       .id(request.getProductId())
                                       .build())
                .createdBy("admin") // TODO: Get current user
                .deleted(false)
                       .build();
    }

    public StockMvtResponse toResponse(final StockMvt entity) {
        return StockMvtResponse.builder()
                .id(entity.getId())
                               .dateMvt(entity.getDateMvt())
                               .comment(entity.getComment())
                               .typeMvt(entity.getTypeMvt())
                               .quantity(entity.getQuantity())
                               .build();
    }
}
