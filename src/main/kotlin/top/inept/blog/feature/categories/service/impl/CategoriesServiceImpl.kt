package top.inept.blog.feature.categories.service.impl

import com.querydsl.core.BooleanBuilder
import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import top.inept.blog.exception.BusinessException
import top.inept.blog.exception.error.CategoriesErrorCode
import top.inept.blog.exception.error.CommonErrorCode
import top.inept.blog.extensions.toPageRequest
import top.inept.blog.feature.categories.model.convert.toCategories
import top.inept.blog.feature.categories.model.dto.CreateCategoriesDTO
import top.inept.blog.feature.categories.model.dto.QueryCategoriesDTO
import top.inept.blog.feature.categories.model.dto.UpdateCategoriesDTO
import top.inept.blog.feature.categories.model.entity.Categories
import top.inept.blog.feature.categories.model.entity.QCategories
import top.inept.blog.feature.categories.model.entity.constraints.CategoriesConstraints
import top.inept.blog.feature.categories.repository.CategoriesRepository
import top.inept.blog.feature.categories.service.CategoriesService

@Service
class CategoriesServiceImpl(
    private val categoriesRepository: CategoriesRepository,
) : CategoriesService {
    override fun getCategories(dto: QueryCategoriesDTO): Page<Categories> {
        val pageRequest = dto.toPageRequest()
        val c = QCategories.categories

        val builder = BooleanBuilder().apply {
            dto.keyword?.takeIf { it.isNotBlank() }?.let { kw ->
                and(c.slug.contains(kw).or(c.name.contains(kw)))
            }
        }

        return categoriesRepository.findAll(builder, pageRequest)
    }

    override fun getCategoriesById(id: Long): Categories {
        //根据id查找分类
        return categoriesRepository.findByIdOrNull(id)
            ?: throw BusinessException(CategoriesErrorCode.ID_NOT_FOUND, id)
    }

    override fun createCategory(dto: CreateCategoriesDTO): Categories {
        val dbCategories = dto.toCategories()

        saveAndFlushCategoryOrThrow(dbCategories)

        return dbCategories
    }

    override fun updateCategory(id: Long, dto: UpdateCategoriesDTO): Categories {
        //根据id查找分类
        val dbCategories = getCategoriesById(id)

        dbCategories.apply {
            dto.name?.let { name = it }
            dto.slug?.let { slug = it }
        }

        saveAndFlushCategoryOrThrow(dbCategories)

        return dbCategories
    }

    override fun deleteCategory(id: Long) {
        //根据id判断分类是否存在
        if (!categoriesRepository.existsById(id)) throw BusinessException(CategoriesErrorCode.ID_NOT_FOUND, id)

        //删除分类
        categoriesRepository.deleteById(id)
    }

    private fun saveAndFlushCategoryOrThrow(dbCategories: Categories): Categories {
        return try {
            categoriesRepository.saveAndFlush(dbCategories)
        } catch (e: DataIntegrityViolationException) {
            val violation = e.cause as? ConstraintViolationException
            when (violation?.constraintName) {
                CategoriesConstraints.UNIQUE_NAME ->
                    throw BusinessException(CategoriesErrorCode.NAME_DB_DUPLICATE, dbCategories.name)

                CategoriesConstraints.UNIQUE_SLUG ->
                    throw BusinessException(CategoriesErrorCode.SLUG_DB_DUPLICATE, dbCategories.slug)

                else -> throw BusinessException(CommonErrorCode.UNKNOWN)
            }
        }
    }
}