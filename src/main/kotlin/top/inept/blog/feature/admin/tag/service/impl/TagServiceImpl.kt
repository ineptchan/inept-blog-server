package top.inept.blog.feature.admin.tag.service.impl

import org.springframework.context.support.MessageSourceAccessor
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import top.inept.blog.extensions.get
import top.inept.blog.feature.admin.tag.pojo.entity.Tag
import top.inept.blog.feature.admin.tag.repository.TagRepository
import top.inept.blog.feature.admin.tag.service.TagService

@Service
class TagServiceImpl(
    private val tagRepository: TagRepository,
    private val messages: MessageSourceAccessor,
) : TagService {
    override fun getTags(): List<Tag> = tagRepository.findAll()

    override fun getTagById(id: Long): Tag {
        //根据id查找标签
        val tag = tagRepository.findByIdOrNull(id)

        //判断标签是否存在
        if (tag == null) throw Exception(messages["message.tag.tag_not_found"])

        return tag
    }

    override fun createTag(tag: Tag): Tag {
        //初次判断标签名称与标签slug是否重复
        if (tagRepository.existsByNameOrSlug(tag.name, tag.slug)) {
            //判断标签名称是否重复
            if (tagRepository.existsByName(tag.name)) throw Exception(messages["message.tag.duplicate_name"])

            //判断标签slug是否重复
            if (tagRepository.existsBySlug(tag.slug)) throw Exception(messages["message.tag.duplicate_slug"])
        }

        return tagRepository.save(tag)
    }

    override fun updateTag(tag: Tag): Tag {
        //根据id查找标签
        val dbTag = tagRepository.findByIdOrNull(tag.id)

        //判断标签是否存在
        if (dbTag == null) throw Exception(messages["message.tag.tag_not_found"])

        //初次判断标签名称与标签slug是否重复
        if (tagRepository.existsByNameOrSlug(tag.name, tag.slug)) {
            //判断标签名称是否重复
            if (tag.name != dbTag.name && tagRepository.existsByName(tag.name)) throw Exception(messages["message.tag.duplicate_name"])

            //判断标签slug是否重复
            if (tag.slug != dbTag.slug && tagRepository.existsBySlug(tag.slug)) throw Exception(messages["message.tag.duplicate_slug"])
        }

        return tagRepository.save(tag)
    }

    override fun deleteTag(id: Long) {
        //根据id判断标签是否存在
        if (!tagRepository.existsById(id))
            throw Exception(messages["message.tag.not_found"])

        //删除标签
        tagRepository.deleteById(id)
    }
}