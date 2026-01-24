package top.inept.blog.feature.tag.service.impl

import com.querydsl.core.BooleanBuilder
import org.hibernate.exception.ConstraintViolationException
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import top.inept.blog.exception.DbDuplicateException
import top.inept.blog.exception.NotFoundException
import top.inept.blog.extensions.get
import top.inept.blog.extensions.toPageRequest
import top.inept.blog.feature.tag.model.convert.toTag
import top.inept.blog.feature.tag.model.dto.CreateTagDTO
import top.inept.blog.feature.tag.model.dto.QueryTagDTO
import top.inept.blog.feature.tag.model.dto.UpdateTagDTO
import top.inept.blog.feature.tag.model.entity.QTag
import top.inept.blog.feature.tag.model.entity.Tag
import top.inept.blog.feature.tag.model.entity.constraints.TagConstraints
import top.inept.blog.feature.tag.repository.TagRepository
import top.inept.blog.feature.tag.service.TagService

@Service
class TagServiceImpl(
    private val tagRepository: TagRepository,
    private val messages: MessageSourceAccessor,
) : TagService {
    override fun getTags(dto: QueryTagDTO): Page<Tag> {
        val pageRequest = dto.toPageRequest()
        val t = QTag.tag

        val builder = BooleanBuilder().apply {
            dto.keyword?.takeIf { it.isNotBlank() }?.let { kw ->
                and(t.name.contains(kw).or(t.slug.containsIgnoreCase(kw)))
            }
        }

        return tagRepository.findAll(builder, pageRequest)
    }

    override fun getTagById(id: Long): Tag {
        //根据id查找标签
        val tag = tagRepository.findByIdOrNull(id) ?: throw NotFoundException(messages["message.tag.tag_not_found"])

        return tag
    }

    override fun createTag(dto: CreateTagDTO): Tag {
        val dbTag = dto.toTag()

        saveAndFlushTagOrThrow(dbTag)

        return dbTag
    }

    override fun updateTag(id: Long, dto: UpdateTagDTO): Tag {
        //根据id查找标签
        val dbTag =
            tagRepository.findByIdOrNull(id) ?: throw NotFoundException(messages["message.tag.tag_not_found"])

        dbTag.apply {
            dto.name?.let { name = it }
            dto.slug?.let { slug = it }
        }

        saveAndFlushTagOrThrow(dbTag)

        return dbTag
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

    private fun saveAndFlushTagOrThrow(dbTag: Tag): Tag {
        return try {
            tagRepository.saveAndFlush(dbTag)
        } catch (e: DataIntegrityViolationException) {
            val violation = e.cause as? ConstraintViolationException
            when (violation?.constraintName) {
                TagConstraints.UNIQUE_NAME -> throw DbDuplicateException(dbTag.name)
                TagConstraints.UNIQUE_SLUG -> throw DbDuplicateException(dbTag.slug)
                else -> throw Exception(messages["message.common.unknown_error"])
            }
        }

    }
}