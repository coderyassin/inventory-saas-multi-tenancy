package org.yascode.inventory_saas.responses;

import lombok.*;
import org.yascode.inventory_saas.entities.TypeMvt;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockMvtResponse {

    private String id;
    private TypeMvt typeMvt;
    private Integer quantity;
    private LocalDate dateMvt;
    private String comment;

}
