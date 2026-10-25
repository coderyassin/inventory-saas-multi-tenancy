package org.yascode.inventory_saas.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.yascode.inventory_saas.config.TenantContext;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.UUID;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@FilterDef(
        name = "tenantFilter",
        parameters = @ParamDef(name = "tenantId", type = String.class),
        defaultCondition = "tenant_id = :tenantId"
)
@Filter(name = "tenantFilter")
public class AbstractEntity {

    @Id
    //@GeneratedValue(strategy = UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false, nullable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", insertable = false)
    private String updatedBy;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
        if (this.deleted == null) {
            this.deleted = Boolean.FALSE;
        }
        // todo: this has to be deleted once security is implemented
        if (this.createdBy == null) {
            this.createdBy = "SYSTEM";
        }

        if (this.tenantId == null) {
            this.tenantId = resolveTenantId();
        }
    }

    protected String resolveTenantId() {
        return TenantContext.getCurrentTenant();
    }
}
