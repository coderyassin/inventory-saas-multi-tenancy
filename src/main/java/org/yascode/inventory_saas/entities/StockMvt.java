package org.yascode.inventory_saas.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

import static jakarta.persistence.EnumType.STRING;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(
        name = "stock_mvts",
        indexes = {
                @Index(name = "idx_stock_mvts_tenant_id", columnList = "tenant_id")
        }
)
public class StockMvt extends AbstractEntity {

    @Column(name = "type_mvt", nullable = false)
    @Enumerated(STRING)
    private TypeMvt typeMvt;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "date_mvt", nullable = false)
    private LocalDate dateMvt;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

}
