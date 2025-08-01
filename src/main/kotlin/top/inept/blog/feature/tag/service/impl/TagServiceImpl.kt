package top.inept.blog.feature.tag.service.impl

import org.springframework.context.support.MessageSourceAccessor
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import top.inept.blog.base.QueryBuilder
import top.inept.blog.exception.NotFoundException
import top.inept.blog.extensions.get
import top.inept.blog.feature.tag.pojo.convert.toTag
import top.inept.blog.feature.tag.pojo.dto.CreateTagDTO
import top.inept.blog.feature.tag.pojo.dto.TagQueryDTO
import top.inept.blog.feature.tag.pojo.dto.UpdateTagDTO
import top.inept.blog.feature.tag.pojo.entity.Tag
import top.inept.blog.feature.tag.repository.TagRepository
import top.inept.blog.feature.tag.repository.TagSpecs
import top.inept.blog.feature.tag.service.TagService

@Service
class TagServiceImpl(
    private val tagRepository: TagRepository,
    private val messages: MessageSourceAccessor,
) : TagService {
    override fun getTags(tagQueryDTO: TagQueryDTO): Page<Tag> {
        val pageRequest = PageRequest.of(tagQueryDTO.page - 1, tagQueryDTO.size)

        val specs = QueryBuilder<Tag>()
            .and(TagSpecs.nameOrSlugContains(tagQueryDTO.keyword))
            .buildSpec()

        return tagRepository.findAll(specs, pageRequest)
    }

    override fun getTagById(id: Long): Tag {
        //根据id查找标签
        val tag = tagRepository.findByIdOrNull(id)

        //判断标签是否存在
        if (tag == null) throw NotFoundException(messages["message.tag.tag_not_found"])

        return tag
    }

    override fun createTag(createTagDTO: CreateTagDTO): Tag {
        //初次判断标签名称与标签slug是否重复
        if (tagRepository.existsByNameOrSlug(createTagDTO.name, createTagDTO.slug)) {
            //判断标签名称是否重复
            if (tagRepository.existsByName(createTagDTO.name)) throw Exception(messages["message.tag.duplicate_name"])

            //判断标签slug是否重复
            if (tagRepository.existsBySlug(createTagDTO.slug)) throw Exception(messages["message.tag.duplicate_slug"])
        }

        return tagRepository.save(createTagDTO.toTag())
    }

    override fun updateTag(updateTagDTO: UpdateTagDTO): Tag {
        //根据id查找标签
        val dbTag = tagRepository.findByIdOrNull(updateTagDTO.id)

        //判断标签是否存在
        if (dbTag == null) throw NotFoundException(messages["message.tag.tag_not_found"])

        //初次判断标签名称与标签slug是否重复
        if (tagRepository.existsByNameOrSlug(updateTagDTO.name, updateTagDTO.slug)) {
            //判断标签名称是否重复
            if (updateTagDTO.name != dbTag.name && tagRepository.existsByName(updateTagDTO.name)) throw Exception(
                messages["message.tag.duplicate_name"]
            )

            //判断标签slug是否重复
            if (updateTagDTO.slug != dbTag.slug && tagRepository.existsBySlug(updateTagDTO.slug)) throw Exception(
                messages["message.tag.duplicate_slug"]
            )
        }

        return tagRepository.save(updateTagDTO.toTag())
    }

    override fun deleteTag(id: Long) {
        //根据id判断标签是否存在
        if (!tagRepository.existsById(id)) throw Exception(messages["message.tag.not_found"])

        //删除标签
        tagRepository.deleteById(id)
    }

    override fun getTagsByIds(ids: List<Long>): List<Tag> {
        //一次查询多个id
        val tags = tagRepository.findAllById(ids)

        //判断是否查询的全面
        if (tags.size != ids.size) {
            val foundIds = tags.map { it.id }.toSet()
            val notFoundIds = ids.filterNot { foundIds.contains(it) }
            throw NotFoundException(messages["message.tag.tags_not_found", notFoundIds.joinToString()])
        }

        return tags
    }
}