package org.yascode.inventory_saas.mappers;

import org.springframework.stereotype.Component;
import org.yascode.inventory_saas.entities.Category;
import org.yascode.inventory_saas.requests.CategoryRequest;
import org.yascode.inventory_saas.responses.CategoryResponse;

@Component
public class CategoryMapper {

    public Category toEntity(final CategoryRequest request) {
        return Category.builder()
                       .name(request.getName())
                       .description(request.getDescription())
                       .deleted(false)
                       .build();
    }

    public CategoryResponse toResponse(final Category entity) {
        final int nbProduct = 0;// entity.getProducts() == null ? 0 : entity.getProducts().size();
        return CategoryResponse.builder()
                               .id(entity.getId())
                               .name(entity.getName())
                               .description(entity.getDescription())
                               .nbProducts(nbProduct)
                               .build();
    }
}
