package org.yascode.inventory_saas.responses;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponse {

    private String id;
    private String name;
    private String description;
    private int nbProducts;
}
