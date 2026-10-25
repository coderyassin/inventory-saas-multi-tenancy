-- categories
alter table categories
    add tenant_id varchar(255) not null;

comment on column categories.tenant_id is 'Tenant ID';

-- products
alter table products
    add tenant_id varchar(255) not null;

comment on column products.tenant_id is 'Tenant ID';

-- stock_mvts
alter table stock_mvts
    add tenant_id varchar(255) not null;

comment on column stock_mvts.tenant_id is 'Tenant ID';