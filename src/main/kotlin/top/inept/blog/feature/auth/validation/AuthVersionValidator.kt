package top.inept.blog.feature.auth.validation

import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import top.inept.blog.feature.auth.constant.JwtClaimConstants
import top.inept.blog.feature.user.repository.UserRepository

@Component
class AuthVersionValidator(
    private val userRepository: UserRepository,
) : OAuth2TokenValidator<Jwt> {

    override fun validate(jwt: Jwt): OAuth2TokenValidatorResult {
        /*        val userId = (jwt.claims[JwtClaimConstants.USER_ID] as? String)
                    ?.toLongOrNull()
                    ?.takeIf { it > 0 }
                    ?: return invalid()*/

        val userId = jwt.claims[JwtClaimConstants.USER_ID]
            ?.toString()
            ?.toLongOrNull()
            ?.takeIf { it > 0 }
            ?: return invalid()

        val tokenVersion = jwt.claims[JwtClaimConstants.AUTH_VERSION]
            ?.toString()
            ?.toLongOrNull()
            ?.takeIf { it >= 0 }
            ?: return invalid()

        val state = userRepository.findAuthStateByUserId(userId)
            ?: return invalid()

        return if (state.status && state.authVersion == tokenVersion) {
            OAuth2TokenValidatorResult.success()
        } else {
            invalid()
        }
    }

    private fun invalid(): OAuth2TokenValidatorResult =
        OAuth2TokenValidatorResult.failure(
            OAuth2Error(
                "invalid_token",
                "Access token is no longer valid",
                null,
            )
        )
}