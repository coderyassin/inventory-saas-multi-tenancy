create index idx_categories_tenant_id
       on categories (tenant_id);

    create index idx_products_tenant_id
       on products (tenant_id);

    create index idx_stock_mvts_tenant_id
       on stock_mvts (tenant_id);