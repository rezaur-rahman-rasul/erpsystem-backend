create table if not exists organization_access_assignments (
    id uuid primary key,
    user_id uuid not null,
    legal_entity_id uuid not null,
    branch_id uuid,
    primary_access boolean not null,
    created_at timestamp,
    created_by varchar(100),
    updated_at timestamp,
    updated_by varchar(100),
    version bigint,
    constraint fk_org_access_user
        foreign key (user_id) references user_accounts (id) on delete cascade
);

create unique index if not exists uk_org_access_user_legal_branch
    on organization_access_assignments (user_id, legal_entity_id, branch_id);
