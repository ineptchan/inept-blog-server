package top.inept.blog.feature.auth.service

import top.inept.blog.feature.auth.model.LoginBundle
import top.inept.blog.feature.auth.model.dto.AuthLoginDTO

interface AuthService {
    fun login(dto: AuthLoginDTO): LoginBundle
    fun refresh(token: String): String
    fun logout(token: String)
}