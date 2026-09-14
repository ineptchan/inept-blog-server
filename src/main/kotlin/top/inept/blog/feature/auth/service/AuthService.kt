package top.inept.blog.feature.auth.service

import top.inept.blog.feature.auth.model.dto.AuthLoginDTO
import top.inept.blog.feature.auth.model.dto.LoginResult
import top.inept.blog.feature.auth.model.dto.RefreshResult

interface AuthService {
    fun login(dto: AuthLoginDTO): LoginResult
    fun refreshToken(refreshTokenString: String): RefreshResult
    fun logout(refreshToken: String)
}