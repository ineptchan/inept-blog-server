package top.inept.blog.feature.rbac.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import top.inept.blog.feature.rbac.model.dto.QueryRoleDTO

@Tag(name = "角色接口")
@RestController
@RequestMapping("/role")
@Validated
class RoleController {

    @PreAuthorize("hasAuthority('admin:root')")
    @Operation(summary = "获取角色列表")
    @GetMapping
    fun getRoles(@Valid dto: QueryRoleDTO): ResponseEntity<String> {


        TODO()
    }
}