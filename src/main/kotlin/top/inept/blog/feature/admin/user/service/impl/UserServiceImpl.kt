package top.inept.blog.feature.admin.user.service.impl

import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import top.inept.blog.constant.JwtClaimsConstant
import top.inept.blog.constant.UserConstant
import top.inept.blog.feature.admin.user.pojo.dto.UserLoginDTO
import top.inept.blog.feature.admin.user.pojo.entity.User
import top.inept.blog.feature.admin.user.pojo.vo.UserLoginVO
import top.inept.blog.properties.JwtProperties
import top.inept.blog.feature.admin.user.repository.UserRepository
import top.inept.blog.feature.admin.user.service.UserService
import top.inept.blog.utils.JwtUtil
import top.inept.blog.utils.PasswordUtil


@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val jwtProperties: JwtProperties
) : UserService {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun getUsers() = userRepository.findAll()

    override fun getUserById(id: Long) = userRepository.findByIdOrNull(id)

    override fun createUser(user: User): User {
        val isUsername = userRepository.existsByUsername(user.username)
        if (isUsername) {
            throw Exception(UserConstant.DUPLICATE_USERNAME)
        }

        return userRepository.save(user)
    }

    override fun updateUser(user: User): User {
        //判断这个id的用户存不存在
        val isUser = userRepository.existsById(user.id)
        if (!isUser) {
            throw Exception(UserConstant.UNKNOWN_ID)
        }

        //判断用户名存不存在
        val isUsername = userRepository.existsByUsername(user.username)
        if (isUsername) {
            throw Exception(UserConstant.DUPLICATE_USERNAME)
        }

        val save = userRepository.save(user)

        return save
    }

    override fun deleteUserById(id: Long) = userRepository.deleteById(id)

    override fun loginUser(userLoginDTO: UserLoginDTO): UserLoginVO {
        val dbUser = userRepository.findByUsername(userLoginDTO.username)
        //没有用户
        if (dbUser == null) {
            throw Exception(UserConstant.USERNAME_OR_PASSWORD_ERROR)
        }

        //密码错误
        if (dbUser.password != PasswordUtil.formatPassword(userLoginDTO.password)) {
            throw Exception(UserConstant.USERNAME_OR_PASSWORD_ERROR)
        }

        //生成token
        val payload = HashMap<String, Any>()
        payload.put(JwtClaimsConstant.ADMIN_ID, dbUser.id)
        payload.put(JwtClaimsConstant.ADMIN_NAME, dbUser.username)

        val token = JwtUtil.createAdminJWT(
            secretKey = jwtProperties.adminSecretKey,
            ttlHours = jwtProperties.adminTtlHours,
            claims = payload
        )

        return UserLoginVO(
            id = dbUser.id,
            username = dbUser.username,
            token = token
        )
    }
}