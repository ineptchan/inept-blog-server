package top.inept.blog.feature.categories.service.impl

import org.hibernate.exception.ConstraintViolationException
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import top.inept.blog.base.QueryBuilder
import top.inept.blog.exception.DbDuplicateException
import top.inept.blog.exception.NotFoundException
import top.inept.blog.extensions.get
import top.inept.blog.extensions.toPageRequest
import top.inept.blog.feature.categories.model.convert.toCategories
import top.inept.blog.feature.categories.model.dto.CreateCategoriesDTO
import top.inept.blog.feature.categories.model.dto.QueryCategoriesDTO
import top.inept.blog.feature.categories.model.dto.UpdateCategoriesDTO
import top.inept.blog.feature.categories.model.entity.Categories
import top.inept.blog.feature.categories.model.entity.constraints.CategoriesConstraints
import top.inept.blog.feature.categories.repository.CategoriesRepository
import top.inept.blog.feature.categories.repository.CategoriesSpecs
import top.inept.blog.feature.categories.service.CategoriesService

@Service
class CategoriesServiceImpl(
    private val categoriesRepository: CategoriesRepository,
    private val messages: MessageSourceAccessor,
) : CategoriesService {
    override fun getCategories(dto: QueryCategoriesDTO): Page<Categories> {
        val pageRequest = dto.toPageRequest()

        val specs = QueryBuilder<Categories>()
            .or(
                CategoriesSpecs.nameContains(dto.keyword),
                CategoriesSpecs.slugContains(dto.keyword),
            )
            .buildSpec()

        return categoriesRepository.findAll(specs, pageRequest)
    }

    override fun getCategoriesById(id: Long): Categories {
        //根据id查找分类
        val categories = categoriesRepository.findByIdOrNull(id)
            ?: throw NotFoundException(messages["message.categories.categories_not_found"])

        return categories
    }

    override fun createCategory(dto: CreateCategoriesDTO): Categories {
        val dbCategories = dto.toCategories()

        try {
            categoriesRepository.saveAndFlush(dbCategories)
        } catch (e: DataIntegrityViolationException) {
            val violation = e.cause as? ConstraintViolationException
            when (violation?.constraintName) {
                CategoriesConstraints.UNIQUE_NAME -> throw DbDuplicateException(dbCategories.name)
                CategoriesConstraints.UNIQUE_SLUG -> throw DbDuplicateException(dbCategories.slug)
            }
        }

        return dbCategories
    }

    override fun updateCategory(id: Long, dto: UpdateCategoriesDTO): Categories {
        //根据id查找分类
        val dbCategories = categoriesRepository.findByIdOrNull(id)
            ?: throw NotFoundException(messages["message.categories.categories_not_found"])

        dbCategories.apply {
            dto.name?.let { name = it }
            dto.slug?.let { slug = it }
        }

        try {
            categoriesRepository.saveAndFlush(dbCategories)
        } catch (e: DataIntegrityViolationException) {
            val violation = e.cause as? ConstraintViolationException
            when (violation?.constraintName) {
                CategoriesConstraints.UNIQUE_NAME -> throw DbDuplicateException(dbCategories.name)
                CategoriesConstraints.UNIQUE_SLUG -> throw DbDuplicateException(dbCategories.slug)
            }
        }

        return dbCategories
    }

    override fun deleteCategory(id: Long) {
        //根据id判断分类是否存在
        if (!categoriesRepository.existsById(id)) throw NotFoundException(messages["message.categories.categories_not_found"])

        //删除分类
        categoriesRepository.deleteById(id)
    }
}