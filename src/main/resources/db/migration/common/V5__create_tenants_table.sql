create table tenants (
                         deleted boolean not null,
                         created_at timestamp(6) not null,
                         updated_at timestamp(6),
                         admin_email varchar(255) not null unique,
                         admin_full_name varchar(255) not null,
                         admin_password varchar(255) not null,
                         admin_username varchar(255) not null unique,
                         company_code varchar(255) not null unique,
                         company_name varchar(255) not null,
                         created_by varchar(255) not null,
                         email varchar(255) not null unique,
                         id varchar(255) not null,
                         status varchar(255) not null check ((status in ('PENDING','ACTIVE','SUSPENDED','INACTIVE'))),
                         tenant_id varchar(255) not null,
                         updated_by varchar(255),
                         primary key (id)
);

create index idx_tenants_tenant_id
    on tenants (tenant_id);

alter table if exists users
    add constraint fk_user_tenant_id
        foreign key (tenant_id)
            references tenants;