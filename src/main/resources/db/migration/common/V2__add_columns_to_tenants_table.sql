alter table tenants
    add created_by varchar(255) not null default 'system';

alter table tenants
    add updated_by varchar(255);