package top.inept.blog.feature.admin.categories.service.impl

import org.springframework.context.support.MessageSourceAccessor
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import top.inept.blog.extensions.get
import top.inept.blog.feature.admin.categories.pojo.entity.Categories
import top.inept.blog.feature.admin.categories.repository.CategoriesRepository
import top.inept.blog.feature.admin.categories.service.CategoriesService

@Service
class CategoriesServiceImpl(
    private val categoriesRepository: CategoriesRepository,
    private val messages: MessageSourceAccessor,
) : CategoriesService {
    override fun getCategories(): List<Categories> = categoriesRepository.findAll()

    override fun getCategoriesById(id: Long): Categories {
        //根据id查找分类
        val categories = categoriesRepository.findByIdOrNull(id)

        //判断分类是否存在
        if (categories == null) throw Exception(messages["message.categories.categories_not_found"])

        return categories
    }

    override fun createCategory(categories: Categories): Categories {
        //判断分类名称是否重复
        if (categoriesRepository.existsByName(categories.name))
            throw Exception(messages["message.categories.duplicate_name"])

        return categoriesRepository.save(categories)
    }

    override fun updateCategory(categories: Categories): Categories {
        //根据id查找分类
        val dbCategories = categoriesRepository.findByIdOrNull(categories.id)

        //判断分类是否存在
        if (dbCategories == null) throw Exception(messages["message.categories.categories_not_found"])

        //判断分类名称是否重复
        if (categories.name != dbCategories.name && categoriesRepository.existsByName(categories.name))
            throw Exception(messages["message.categories.duplicate_name"])

        return categoriesRepository.save(categories)
    }

    override fun deleteCategory(id: Long) {
        //判断分类是否存在
        if (!categoriesRepository.existsById(id))
            throw Exception(messages["message.categories.not_found"])

        //删除分类
        categoriesRepository.deleteById(id)
    }
}