package top.inept.blog.feature.auth.service

import top.inept.blog.feature.auth.model.LoginBundle
import top.inept.blog.feature.auth.model.dto.AuthLoginDTO

interface AuthService {
    fun login(dto: AuthLoginDTO): LoginBundle
    fun refresh(refreshToken: String): String
    fun logout(refreshToken: String)
}