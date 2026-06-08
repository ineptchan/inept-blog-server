insert into permission_table(code, name)
values ('admin:article:read', '管理员读取文章'),
       ('admin:article:create', '管理员创建文章'),
       ('admin:article:update', '管理员更新文章'),
       ('admin:article:delete', '管理员删除文章'),
       ('admin:article:upload:image', '管理员上传文章图片'),
       ('admin:article:upload:featured-image', '管理员上传文章封面图片'),
       ('admin:article:upload:video', '管理员上传文章视频'),
       ('admin:article:upload:attachment', '管理员上传文章附件'),

       ('admin:categories:read', '管理员读取分类'),
       ('admin:categories:create', '管理员创建分类'),
       ('admin:categories:update', '管理员更新分类'),
       ('admin:categories:delete', '管理员删除分类'),

       ('admin:comment:read', '管理员读取评论'),
       ('admin:comment:create', '管理员创建评论'),
       ('admin:comment:update', '管理员更新评论'),
       ('admin:comment:delete', '管理员删除评论'),
       ('user:comment:create', '用户创建评论'),
       ('user:comment:like', '用户点赞评论'),

       ('admin:tag:read', '管理员读取标签'),
       ('admin:tag:create', '管理员创建标签'),
       ('admin:tag:update', '管理员更新标签'),
       ('admin:tag:delete', '管理员删除标签'),

       ('admin:user:read', '管理员读取用户'),
       ('admin:user:create', '管理员创建用户'),
       ('admin:user:update', '管理员更新用户'),
       ('admin:user:delete', '管理员删除用户'),
       ('admin:user:role:update', '管理员更新用户角色'),
       ('admin:user:role:delete', '管理员删除用户角色'),

       ('user:user:read', '用户读取用户资料'),
       ('user:user:update', '用户更新用户资料'),

       ('admin:role:read', '管理员读取角色'),
       ('admin:role:create', '管理员创建角色'),
       ('admin:role:update', '管理员更新角色'),
       ('admin:role:delete', '管理员删除角色'),
       ('admin:role:permission:read', '管理员读取角色权限'),
       ('admin:role:permission:update', '管理员更新角色权限'),
       ('admin:role:permission:delete', '管理员删除角色权限'),

       ('admin:permission:read', '管理员读取权限'),
       ('admin:permission:update', '管理员更新权限');

--- 创建角色
insert into role_table(code, description, name)
values ('admin', '管理员', '管理员'),
       ('user', '用户', '用户');

--- 绑定权限到admin角色
insert into role_permission_table(created_at, updated_at, permission_id, role_id)
select current_timestamp, null, p.id, r.id
from permission_table p
         join role_table r on r.code = 'admin'
where p.code like 'admin:%'
  and not exists (select 1
                  from role_permission_table rp
                  where rp.permission_id = p.id
                    and rp.role_id = r.id);

--- 绑定权限到user角色
insert into role_permission_table(created_at, updated_at, permission_id, role_id)
select current_timestamp, null, p.id, r.id
from permission_table p
         join role_table r on r.code = 'user'
where p.code like 'user:%'
  and not exists (select 1
                  from role_permission_table rp
                  where rp.permission_id = p.id
                    and rp.role_id = r.id);

--- 创建管理用户 admintest admin123456
insert into user_table(created_at, email, nickname, password, updated_at, username, status)
values (now(), 'admin@inept.top', 'inept', '$2a$10$P53CwaeHtpaPxUU9fiBOPOVTHuh7e1PWJ.D3ZV9HSeGMn6ryliQai', null,
        'admintest', true);

--分配角色给admintest
insert into user_role_table(created_at, role_id, user_id)
select current_timestamp, r.id, u.id
from role_table r
         join user_table u on u.username = 'admintest'
where r.code = 'admin'
  and not exists (select 1
                  from user_role_table ur
                  where ur.role_id = r.id
                    and ur.user_id = u.id);


insert into user_role_table(created_at, role_id, user_id)
select current_timestamp, r.id, u.id
from role_table r
         join user_table u on u.username = 'admintest'
where r.code = 'user'
  and not exists (select 1
                  from user_role_table ur
                  where ur.role_id = r.id
                    and ur.user_id = u.id);