insert into permissions(code, created_at, name)
values ('admin:article:read', now(), '管理员读取文章'),
       ('admin:article:write', now(), '管理员创建文章'),
       ('admin:article:modify', now(), '管理员更新文章'),
       ('admin:article:delete', now(), '管理员删除文章'),
       ('admin:categories:read', now(), '管理员读取分类'),

       ('admin:categories:write', now(), '管理员创建分类'),
       ('admin:categories:modify', now(), '管理员更新分类'),
       ('admin:categories:delete', now(), '管理员删除分类'),

       ('admin:comment:read', now(), '管理员读取评论'),
       ('admin:comment:write', now(), '管理员创建评论'),
       ('admin:comment:modify', now(), '管理员更新评论'),
       ('admin:comment:delete', now(), '管理员删除评论'),
       ('user:comment:write', now(), '用户创建评论'),

       ('admin:tag:read', now(), '管理员读取标签'),
       ('admin:tag:write', now(), '管理员创建标签'),
       ('admin:tag:modify', now(), '管理员更新标签'),
       ('admin:tag:delete', now(), '管理员删除标签'),

       ('admin:user:read', now(), '管理员读取用户'),
       ('admin:user:write', now(), '管理员创建用户'),
       ('admin:user:modify', now(), '管理员更新用户'),
       ('admin:user:delete', now(), '管理员删除用户'),

       ('user:user:read', now(), '用户读取用户'),
       ('user:user:modify', now(), '用户更新用户'),

       ('admin:file:read', now(), '管理员读取文件'),
       ('admin:file:write', now(), '管理员写入文件'),
       ('admin:file:delete', now(), '管理员删除文件');

--- 创建角色
insert into roles(code, created_at, description, name)
values ('admin', now(), '管理员', '管理员'),
       ('user', now(), '用户', '用户');

--- 绑定权限到admin角色
insert into roles_permissions(created_at, updated_at, permission_id, role_id)
select current_timestamp, null, p.id, r.id
from permissions p
         join roles r on r.code = 'admin'
where p.code like 'admin:%'
  and not exists (
    select 1
    from roles_permissions rp
    where rp.permission_id = p.id
      and rp.role_id = r.id
);

--- 绑定权限到user角色
insert into roles_permissions(created_at, updated_at, permission_id, role_id)
select current_timestamp, null, p.id, r.id
from permissions p
         join roles r on r.code = 'user'
where p.code like 'user:%'
  and not exists (
    select 1
    from roles_permissions rp
    where rp.permission_id = p.id
      and rp.role_id = r.id
);

--- 创建管理用户 admintest admin123456
insert into users(created_at, email, nickname, password, updated_at, username)
values (now(), 'admin@inept.top', 'inept', '$2a$10$P53CwaeHtpaPxUU9fiBOPOVTHuh7e1PWJ.D3ZV9HSeGMn6ryliQai', null,
        'admintest');

--分配角色给admintest
insert into users_roles(created_at, role_id, user_id)
select current_timestamp, r.id, u.id
from roles r
         join users u on u.username = 'admintest'
where r.code = 'admin'
  and not exists (
    select 1
    from users_roles ur
    where ur.role_id = r.id
      and ur.user_id = u.id
);

insert into users_roles(created_at, role_id, user_id)
select current_timestamp, r.id, u.id
from roles r
         join users u on u.username = 'admintest'
where r.code = 'user'
  and not exists (
    select 1
    from users_roles ur
    where ur.role_id = r.id
      and ur.user_id = u.id
);