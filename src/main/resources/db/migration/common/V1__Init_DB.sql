create table categories (
                            deleted boolean not null,
                            created_at timestamp(6) not null,
                            updated_at timestamp(6),
                            created_by varchar(255) not null,
                            description TEXT,
                            id varchar(255) not null,
                            name varchar(255) not null unique,
                            updated_by varchar(255),
                            primary key (id)
);

create table products (
                          alert_threshold integer not null,
                          deleted boolean not null,
                          price numeric(38,2) not null,
                          created_at timestamp(6) not null,
                          updated_at timestamp(6),
                          category_id varchar(255),
                          created_by varchar(255) not null,
                          description TEXT,
                          id varchar(255) not null,
                          name varchar(255) not null,
                          reference varchar(255) not null unique,
                          updated_by varchar(255),
                          primary key (id)
);

create table stock_mvts (
                            date_mvt date not null,
                            deleted boolean not null,
                            quantity integer not null,
                            created_at timestamp(6) not null,
                            updated_at timestamp(6),
                            comment TEXT,
                            created_by varchar(255) not null,
                            id varchar(255) not null,
                            product_id varchar(255),
                            type_mvt varchar(255) not null check ((type_mvt in ('IN','OUT'))),
                            updated_by varchar(255),
                            primary key (id)
);

alter table if exists products
    add constraint FKog2rp4qthbtt2lfyhfo32lsw9
        foreign key (category_id)
            references categories;

alter table if exists stock_mvts
    add constraint FK7fa7lrqrkuwg32dw11l8i3t84
        foreign key (product_id)
            references products;
