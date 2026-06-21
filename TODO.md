1. [ ] 评论审核和反垃圾评论
2. [ ] 实现匿名评论
3. [ ] 实现文章访问量
4. [X] 未传递slug自动尝试生成
5. [X] 创建docker镜像
6. [ ] 为项目添加liquibase支持
7. [ ] 创建用户用邮箱发密码，密码由系统随机生成
8. [ ] 完善测试
9. [X] 添加rbac接口
10. [ ] 改进permissions，添加permissionType，常见：MENU/REST/OTHER
11. [X] 考虑去除错误的code使用msgkey
12. [ ] 压缩文章的视频，尝试使用消息队列，pgsql也可以完成
13. [X] 完全重构file模块为objectstorage
14. [X] 处理管理混乱的application
15. [X] 审计Validated验证
16. [x] 处理项目中的时间管理混乱的问题
17. [X] 改进 ProblemDetail 的 props
18. [X] 添加用户开关是否可用
19. [ ] 搜索功能添加排序
20. [X] `GET /admin/user`添加返回绑定的角色
21. [ ] 访问文章ip记录
22. [X] Flyway / Liquibase
23. [ ] auth相关接口返回token过期时间
24. [ ] 搜索可以用Elasticsearch
25. [ ] 添加删除文章附件 视频。。的接口
26. [X] 升级评论
27. [ ] 文章提交软删除
28. [ ] 添加文章点赞 浏览数
29. [ ] 支持ABAC?
30. [X] 预签名上传，合并时需要判断是否是一个用户的请求
31. [ ] 原生 enum 类型 `@Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.NAMED_ENUM)`
32. [ ] minio权限最小化
33. [ ] docker需要开发镜像

## 对象存储

记得使用s3生命周期删除原始文件

- [X] 头像 公开
- [X] 文章的图片 公开
- [X] 文章的封面 公开
- [X] 文章的视频 公开
- [X] 文章的附件 公开
- [X] 删除文章要删除附件和图片

## objectstorage 重构评审（2026-06-11）

### 安全 / 正确性（优先）

- [ ] `ObjectStorage` 实体补回 `ownerUserId`：presign 时落库申请人，complete 时校验是同一用户（任何登录用户可 complete 别人的
  pending 上传；头像流程在 complete 时才从 SecurityContext 取用户，等于"谁来 complete 头像就归谁"）
- [ ] `completeUpload` 加状态机校验：只允许 `PREPARED → UPLOADED`，防重复 complete（重放）/ complete 已 DELETED 记录，顺便获得幂等性；
  `Status` 里没人写入的 `UPLOADING`/`FAILED` 要么用起来要么删掉
- [ ] complete 时核验实际对象：`statObject` 对比实际大小与 presign 声明的 `fileSize`（presigned PUT
  限制不了实际上传大小）；contentType 白名单可提前到 presign 阶段按 Purpose 拒绝

### 架构

- [ ] handler 策略化拆分，解开反向依赖：`ObjectStorageServiceImpl` 同时承担通用存储操作和各业务后处理，反向依赖了
  user/article feature，`when(purpose)` 会随业务膨胀（已有三个 TODO 分支）。拆成：底层 `ObjectStorageService` 只管
  presign/核验/promote/删除；按 Purpose 注册 `UploadCompletionHandler`（`supports(purpose)` + `handle(record, stream)`
  ），Spring 注入 List 分发——头像 handler 放 user feature，文章 handler 放 article feature
- [ ] 接口里旧的 multipart 方法（`uploadArticleImage` 等五个 `TODO("Not yet implemented")`）和五百行注释代码：确定全面转
  presign 就删掉，别两套范式共存

### 功能缺口 / bug

- [ ] 头像二次上传必挂：objectKey 固定 `${user.id}.webp` + `object_key` 唯一约束 + 每次 complete 都 insert 新行 → 第二次换头像撞
  `UNIQUE_OBJECT_KEY`。要么 upsert，要么 key 带版本号（带版本顺便解决 CDN 缓存失效）
- [ ] `completeUpload` 返回 VO（最终 id、url、objectKey）而不是 `"OK"` 字符串：文章图片场景客户端必须拿到最终 URL
  才能插入正文；记录模型建议"一行代表一个逻辑对象，promote 时原地更新 bucket/objectKey/status"，而不是 pending 行标
  DELETED + 另起新行
- [ ] `@Transactional` 不要包住 MinIO 远程 IO：getObject 读取、webp 压缩、putObject 都在事务里，大文件长时间占用数据库连接。事务只包状态迁移，存储
  IO 放事务外
- [ ] 孤儿对象回收：拿了 presign URL 但从不 complete 的 PREPARED 记录和 pending 对象会无限堆积。给 pending 桶配 MinIO
  生命周期规则（如 1 天过期），数据库侧定时标 FAILED
- [ ] complete 早于客户端实际上传完成时 `getObject` 抛 NoSuchKey，应映射成友好业务错误而不是 500

### 配置 / 小问题

- [ ] 桶名两处定义互相脱节：`Purpose.getBucketName()` 硬编码 + `ObjectStorageProperties.buckets` 各一份。合并成 properties
  里的 `Map<Purpose, 规则>`（桶名、maxFileSize、允许 mime、是否需要 pending），三个 when 表达式收敛成查表；可考虑单桶 + key
  前缀替代 10 个桶
- [ ] presign 有效期 `duration = 5` 硬编码，进 properties
- [ ] `PresignUploadVO.expiresAt` 用 `LocalDateTime` 无时区，对外 API 用 `Instant`/`OffsetDateTime`
- [ ] objectKey 扩展名直接取自客户端文件名未校验（可能为空，key 变成 `uuid.`）
- [ ] 重命名后的 `Status`/`Purpose` 太泛型易混淆，`ObjectStorageStatus` 更好；`fileHash` 唯一约束被注释掉，去重这条线保留还是放弃要明确决定