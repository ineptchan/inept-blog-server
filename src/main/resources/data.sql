insert into permissions(code, created_at, name)
values ('admin:article:read', now(), '管理员读取文章'),
       ('admin:article:create', now(), '管理员创建文章'),
       ('admin:article:update', now(), '管理员更新文章'),
       ('admin:article:delete', now(), '管理员删除文章'),
       ('admin:categories:read', now(), '管理员读取分类'),

       ('admin:categories:create', now(), '管理员创建分类'),
       ('admin:categories:update', now(), '管理员更新分类'),
       ('admin:categories:delete', now(), '管理员删除分类'),

       ('admin:comment:read', now(), '管理员读取评论'),
       ('admin:comment:create', now(), '管理员创建评论'),
       ('admin:comment:update', now(), '管理员更新评论'),
       ('admin:comment:delete', now(), '管理员删除评论'),
       ('user:comment:create', now(), '用户创建评论'),

       ('admin:tag:read', now(), '管理员读取标签'),
       ('admin:tag:create', now(), '管理员创建标签'),
       ('admin:tag:update', now(), '管理员更新标签'),
       ('admin:tag:delete', now(), '管理员删除标签'),

       ('admin:user:read', now(), '管理员读取用户'),
       ('admin:user:create', now(), '管理员创建用户'),
       ('admin:user:update', now(), '管理员更新用户'),
       ('admin:user:delete', now(), '管理员删除用户'),

       ('user:user:read', now(), '用户读取用户'),
       ('user:user:update', now(), '用户更新用户');

--- 创建角色
insert into roles(code, created_at, description, name)
values ('admin', now(), '管理员', '管理员'),
       ('user', now(), '用户', '用户');

--- 绑定权限到admin角色
insert into roles_permissions(created_at, updated_at, permission_id, role_id)
select now(), null, p.id, r.id
from permissions p
         join roles r on r.code = 'admin'
where p.code like 'admin:%'
on conflict (permission_id, role_id) do nothing;

--- 绑定权限到user角色
insert into roles_permissions(created_at, updated_at, permission_id, role_id)
select now(), null, p.id, r.id
from permissions p
         join roles r on r.code = 'user'
where p.code like 'user:%'
on conflict (permission_id, role_id) do nothing;

--- 创建管理用户 admintest admin123456
insert into users(created_at, email, nickname, password, updated_at, username)
values (now(), 'admin@inept.top', 'inept', '$2a$10$P53CwaeHtpaPxUU9fiBOPOVTHuh7e1PWJ.D3ZV9HSeGMn6ryliQai', null,
        'admintest');

--分配角色给admintest
insert into users_roles(created_at, role_id, user_id)
select now(), r.id, u.id
from roles r
         cross join users u
where r.code = 'admin'
  and u.username = 'admintest'
on conflict (role_id,user_id) do nothing;