package top.inept.blog.feature.user.repository

import org.springframework.data.jpa.domain.Specification
import top.inept.blog.feature.user.pojo.entity.User
import top.inept.blog.feature.user.pojo.entity.User_

object UserSpecs {
    fun nicknameContains(keyword: String?): Specification<User>? {
        return keyword?.takeIf { it.isNotBlank() }?.let {
            Specification { root, _, cb ->
                cb.like(cb.lower(root.get(User_.nickname)), "%${it.lowercase()}%")
            }
        }
    }

    fun usernameContains(keyword: String?): Specification<User>? {
        return keyword?.takeIf { it.isNotBlank() }?.let {
            Specification { root, _, cb ->
                cb.like(cb.lower(root.get(User_.username)), "%${it.lowercase()}%")
            }
        }
    }

    fun emailContains(keyword: String?): Specification<User>? {
        return keyword?.takeIf { it.isNotBlank() }?.let {
            Specification { root, _, cb ->
                cb.like(cb.lower(root.get(User_.email)), "%${it.lowercase()}%")
            }
        }
    }
}