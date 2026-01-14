package top.inept.blog.feature.auth.service

import top.inept.blog.feature.auth.model.Combo
import top.inept.blog.feature.auth.model.dto.AuthLoginDTO
import top.inept.blog.feature.auth.model.vo.AuthLoginVO

interface AuthService {
    fun login(dto: AuthLoginDTO): Combo<AuthLoginVO, String>
    fun refresh(token: String): String
}