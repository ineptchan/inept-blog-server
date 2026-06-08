package top.inept.blog.util

import net.datafaker.Faker
import top.inept.blog.feature.rbac.model.dto.CreateRoleDTO
import top.inept.blog.feature.user.model.dto.CreateUserDTO

object DTOGeneration {
    val faker = Faker()

    fun createRoleDTO() = CreateRoleDTO(
        "ROLE_${faker.number().digits(12)}",
        faker.name().name(),
        faker.lorem().paragraph()
    )

    fun createUserDTO(role: List<Long>? = null) = CreateUserDTO(
        faker.name().fullName(),
        faker.credentials().username().replace(".", "_"),
        faker.credentials().password(),
        faker.internet().emailAddress(),
        role
    )
}