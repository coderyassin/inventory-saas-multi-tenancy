package org.yascode.inventory_saas.mappers;

import org.springframework.stereotype.Component;
import org.yascode.inventory_saas.entities.Category;
import org.yascode.inventory_saas.entities.Product;
import org.yascode.inventory_saas.requests.ProductRequest;
import org.yascode.inventory_saas.responses.ProductResponse;

@Component
public class ProductMapper {

    public Product toEntity(final ProductRequest request) {
        return Product.builder()
                      .name(request.getName())
                      .reference(request.getReference())
                      .description(request.getDescription())
                      .price(request.getPrice())
                      .alertThreshold(request.getAlertThreshold())
                      .category(Category.builder()
                                        .id(request.getCategoryId())
                                        .build())
                      .createdBy("admin") // TODO: Get current user
                      .deleted(false)
                      .build();
    }

    public ProductResponse toResponse(final Product product) {
        return ProductResponse.builder()
                              .id(product.getId())
                              .name(product.getName())
                              .reference(product.getReference())
                              .description(product.getDescription())
                              .price(product.getPrice())
                              .alertThreshold(product.getAlertThreshold())
                              .categoryId(product.getCategory()
                                                 .getId())
                              .categoryName(product.getCategory()
                                                   .getName())
                              // .availableQuantity() to be later implemented
                              .build();
    }
}
