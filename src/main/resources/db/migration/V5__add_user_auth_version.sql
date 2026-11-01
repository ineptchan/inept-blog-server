alter table user_table
    add column auth_version bigint not null default 0;