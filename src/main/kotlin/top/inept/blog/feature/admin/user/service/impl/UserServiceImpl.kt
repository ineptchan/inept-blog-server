package top.inept.blog.feature.admin.user.service.impl

import org.slf4j.LoggerFactory
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import top.inept.blog.extensions.get
import top.inept.blog.feature.admin.user.pojo.dto.LoginUserDto
import top.inept.blog.feature.admin.user.pojo.entity.User
import top.inept.blog.feature.admin.user.pojo.vo.LoginUserVo
import top.inept.blog.feature.admin.user.repository.UserRepository
import top.inept.blog.feature.admin.user.service.UserService
import top.inept.blog.properties.JwtProperties
import top.inept.blog.utils.JwtUtil

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val jwtProperties: JwtProperties,
    private val messages: MessageSourceAccessor,
    private val jwtUtil: JwtUtil
) : UserService {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun getUsers() = userRepository.findAll()

    override fun getUserById(id: Long): User {
        val user = userRepository.findByIdOrNull(id)
        if (user == null) {
            throw Exception(messages["user.user_not_found"])
        }
        return user
    }

    override fun createUser(user: User): User {
        val isUsername = userRepository.existsByUsername(user.username)
        if (isUsername) {
            throw Exception(messages["user.duplicate_username"])
        }

        return userRepository.save(user)
    }

    override fun updateUser(user: User): User {
        //判断这个id的用户存不存在
        val isUser = userRepository.existsById(user.id)
        if (!isUser) {
            throw Exception(messages["common.id_does_not_exist"])
        }

        //判断用户名存不存在
        val isUsername = userRepository.existsByUsername(user.username)
        if (isUsername) {
            throw Exception(messages["user.duplicate_username"])
        }

        val save = userRepository.save(user)

        return save
    }

    override fun deleteUserById(id: Long) = userRepository.deleteById(id)

    override fun loginUser(userLoginDTO: LoginUserDto): LoginUserVo {
        val dbUser = userRepository.findByUsername(userLoginDTO.username)
        //没有用户
        if (dbUser == null) {
            throw Exception(messages["user.user_not_found"])
        }

        userLoginDTO

        //校验密码
        val bCryptPasswordEncoder = BCryptPasswordEncoder()
        if (!bCryptPasswordEncoder.matches(userLoginDTO.password, dbUser.password)) {
            throw Exception(messages["user.username_or_password_error"])
        }

        //生成token
        val token = jwtUtil.createJWT(
            secretKey = jwtProperties.secretKey,
            ttlHours = jwtProperties.ttlHours,
            id = dbUser.id,
            username = dbUser.username,
            role = dbUser.role,
        )

        return LoginUserVo(
            id = dbUser.id,
            username = dbUser.username,
            token = token
        )
    }
}