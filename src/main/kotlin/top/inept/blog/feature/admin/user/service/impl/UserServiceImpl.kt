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
        //根据id查找用户
        val user = userRepository.findByIdOrNull(id)

        //判断有没有用户
        if (user == null) throw Exception(messages["message.user.user_not_found"])

        return user
    }

    override fun createUser(user: User): User {
        //判断有没有重复用户名
        if (userRepository.existsByUsername(user.username))
            throw Exception(messages["message.user.duplicate_username"])

        return userRepository.save(user)
    }

    override fun updateUser(user: User): User {
        //根据id查找用户
        val dbUser = userRepository.findByIdOrNull(user.id)

        //判断这个用户是否存在
        if (dbUser == null) throw Exception(messages["message.user.user_not_found"])

        //判断用户名是否存在
        if (user.username != dbUser.username && userRepository.existsByUsername(user.username))
            throw Exception(messages["message.user.duplicate_username"])

        //保存用户
        val save = userRepository.save(user)

        return save
    }

    override fun deleteUserById(id: Long) {
        //判断用户是否存在
        if (!userRepository.existsById(id))
            throw Exception(messages["message.user.user_not_found"])

        //删除用户
        userRepository.deleteById(id)
    }

    override fun loginUser(userLoginDTO: LoginUserDto): LoginUserVo {
        //根据用户名查找用户
        val dbUser = userRepository.findByUsername(userLoginDTO.username)

        //没有用户
        if (dbUser == null) {
            throw Exception(messages["message.user.user_not_found"])
        }

        //校验密码
        val bCryptPasswordEncoder = BCryptPasswordEncoder()
        if (!bCryptPasswordEncoder.matches(userLoginDTO.password, dbUser.password)) {
            throw Exception(messages["message.user.username_or_password_error"])
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