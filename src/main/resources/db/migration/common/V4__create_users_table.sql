create table users (
                       deleted boolean not null,
                       enabled boolean,
                       created_at timestamp(6) not null,
                       updated_at timestamp(6),
                       created_by varchar(255) not null,
                       email varchar(255) not null unique,
                       first_name varchar(255) not null,
                       id varchar(255) not null,
                       last_name varchar(255) not null,
                       password varchar(255) not null,
                       role varchar(255) not null check ((role in ('ROLE_PLATFORM_ADMIN','ROLE_COMPANY_ADMIN','ROLE_ADMINISTRATOR','ROLE_USER','ROLE_SALES_OPERATOR'))),
                       tenant_id varchar(255) not null,
                       updated_by varchar(255),
                       username varchar(255) not null unique,
                       primary key (id)
);

create index idx_users_tenant_id
    on users (tenant_id);

create index idx_users_username
    on users (username);

create index idx_users_email
    on users (email);