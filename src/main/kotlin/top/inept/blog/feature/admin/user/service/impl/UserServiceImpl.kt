package top.inept.blog.feature.admin.user.service.impl

import org.slf4j.LoggerFactory
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import top.inept.blog.exception.NotFoundException
import top.inept.blog.extensions.get
import top.inept.blog.feature.admin.user.pojo.convert.toUser
import top.inept.blog.feature.admin.user.pojo.dto.CreateUserDTO
import top.inept.blog.feature.admin.user.pojo.dto.LoginUserDTO
import top.inept.blog.feature.admin.user.pojo.dto.UpdateUserDTO
import top.inept.blog.feature.admin.user.pojo.entity.User
import top.inept.blog.feature.admin.user.pojo.vo.LoginUserVO
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
        return userRepository.findByIdOrNull(id)?: throw NotFoundException("message.user.user_not_found")
    }

    override fun getUserByUsername(username: String): User {
        //根据username查找用户
        return userRepository.findByUsername(username) ?: throw NotFoundException("message.user.user_not_found")
    }

    override fun createUser(createUserDTO: CreateUserDTO): User {
        //判断有没有重复用户名
        if (userRepository.existsByUsername(createUserDTO.username)) throw Exception(messages["message.user.duplicate_username"])

        //判断邮箱是否存在
        createUserDTO.email?.let { email ->
            if (userRepository.existsByEmail(email)) throw Exception(messages["message.user.duplicate_email"])
        }

        return userRepository.save(createUserDTO.toUser())
    }

    override fun updateUser(updateUserDTO: UpdateUserDTO): User {
        //根据id查找用户
        val dbUser = userRepository.findByIdOrNull(updateUserDTO.id)
            ?: throw Exception(messages["message.user.user_not_found"])

        //判断用户名是否重复
        if (updateUserDTO.username != dbUser.username && userRepository.existsByUsername(updateUserDTO.username))
            throw Exception(messages["message.user.duplicate_username"])

        //判断邮箱是否重复
        updateUserDTO.email?.let { email ->
            if (updateUserDTO.email != dbUser.email && userRepository.existsByEmail(email)) throw Exception(messages["message.user.duplicate_email"])
        }

        return userRepository.save(updateUserDTO.toUser())
    }

    override fun deleteUserById(id: Long) {
        //根据id判断用户是否存在
        if (!userRepository.existsById(id)) throw NotFoundException(messages["message.user.user_not_found"])

        //删除用户
        userRepository.deleteById(id)
    }

    override fun loginUser(userLoginDTO: LoginUserDTO): LoginUserVO {
        //根据用户名查找用户
        val dbUser = userRepository.findByUsername(userLoginDTO.username)

        //没有用户
        if (dbUser == null) throw NotFoundException(messages["message.user.user_not_found"])

        //校验密码
        val bCryptPasswordEncoder = BCryptPasswordEncoder()
        if (!bCryptPasswordEncoder.matches(userLoginDTO.password, dbUser.password))
            throw Exception(messages["message.user.username_or_password_error"])

        //生成token
        val token = jwtUtil.createJWT(
            secretKey = jwtProperties.secretKey,
            ttlHours = jwtProperties.ttlHours,
            id = dbUser.id,
            username = dbUser.username,
            role = dbUser.role,
        )

        return LoginUserVO(
            id = dbUser.id,
            username = dbUser.username,
            token = token
        )
    }
}