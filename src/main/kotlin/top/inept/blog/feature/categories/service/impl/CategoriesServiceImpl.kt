package top.inept.blog.feature.categories.service.impl

import org.springframework.context.support.MessageSourceAccessor
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import top.inept.blog.exception.NotFoundException
import top.inept.blog.extensions.get
import top.inept.blog.feature.categories.pojo.convert.toCategories
import top.inept.blog.feature.categories.pojo.dto.UpdateCategoriesDTO
import top.inept.blog.feature.categories.pojo.dto.CreateCategoriesDTO
import top.inept.blog.feature.categories.pojo.entity.Categories
import top.inept.blog.feature.categories.repository.CategoriesRepository
import top.inept.blog.feature.categories.service.CategoriesService

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
        if (categories == null) throw NotFoundException(messages["message.categories.categories_not_found"])

        return categories
    }

    override fun createCategory(createCategoriesDTO: CreateCategoriesDTO): Categories {
        //初次判断分类名称与分类slug是否重复
        if (categoriesRepository.existsByNameOrSlug(createCategoriesDTO.name, createCategoriesDTO.slug)) {
            //判断分类名称是否重复
            if (categoriesRepository.existsByName(createCategoriesDTO.name)) throw Exception(messages["message.categories.duplicate_name"])

            //判断分类slug是否重复
            if (categoriesRepository.existsBySlug(createCategoriesDTO.slug)) throw Exception(messages["message.categories.duplicate_slug"])
        }

        return categoriesRepository.save(createCategoriesDTO.toCategories())
    }

    override fun updateCategory(updateCategoriesDTO: UpdateCategoriesDTO): Categories {
        //根据id查找分类
        val dbCategories = categoriesRepository.findByIdOrNull(updateCategoriesDTO.id)

        //判断分类是否存在
        if (dbCategories == null) throw NotFoundException(messages["message.categories.categories_not_found"])

        //初次判断分类名称与分类Slug是否重复
        if (categoriesRepository.existsByNameOrSlug(updateCategoriesDTO.name, updateCategoriesDTO.slug)) {
            //判断分类名称是否重复
            if (updateCategoriesDTO.name != dbCategories.name && categoriesRepository.existsByName(updateCategoriesDTO.name))
                throw Exception(messages["message.categories.duplicate_name"])

            //判断分类slug是否重复
            if (updateCategoriesDTO.slug != dbCategories.slug && categoriesRepository.existsBySlug(updateCategoriesDTO.slug))
                throw Exception(messages["message.categories.duplicate_slug"])
        }

        return categoriesRepository.save(updateCategoriesDTO.toCategories())
    }

    override fun deleteCategory(id: Long) {
        //根据id判断分类是否存在
        if (!categoriesRepository.existsById(id)) throw NotFoundException(messages["message.categories.categories_not_found"])

        //删除分类
        categoriesRepository.deleteById(id)
    }
}