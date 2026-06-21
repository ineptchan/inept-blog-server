## 基础信息

- `access token` 默认过期时间是15分钟
- `refresh token` 默认过期时间是 15天

## 登录

```
登录请求
↓
校验用户状态和密码
↓
生成 refresh token, 并且保存RefreshTokenEntity到数据库, 不明文保存, sha256后保存
↓
使用 refresh token 信息生成 access token
↓
refresh token 写入 httpOnly 中, access token 在返回 body 中
```

## 刷新

```
从 httpOnly 中读取 refreshTokenString
↓
token 字符串哈希查询数据库获取RefreshTokenEntity
↓
校验是否合法, 是否被撤销, 是否过期
↓
判断用户状态并获取该用户的 permissionCodes, 生成 access token
↓
记录 refreshToken 最后使用时间到 RefreshTokenEntity
```

可以添加 Refresh Token Rotation 机制

## 退出

```
从 httpOnly 中读取 refreshTokenString
↓
校验是否合法, 是否已经撤销
↓
撤销信息写入RefreshTokenEntity.revokedAt
↓
覆盖清空 httpOnly Cookie 的 X-Refresh-Token
```