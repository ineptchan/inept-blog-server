package top.inept.blog.feature.tag.service


import org.springframework.data.domain.Page
import top.inept.blog.feature.tag.model.dto.CreateTagDTO
import top.inept.blog.feature.tag.model.dto.QueryTagDTO
import top.inept.blog.feature.tag.model.dto.UpdateTagDTO
import top.inept.blog.feature.tag.model.entity.Tag

interface TagService {
    fun getTags(dto: QueryTagDTO): Page<Tag>
    fun getTagById(id: Long): Tag
    fun createTag(dto: CreateTagDTO): Tag
    fun updateTag(id: Long, dto: UpdateTagDTO): Tag
    fun deleteTag(id: Long)
    fun getTagsByIds(ids: List<Long>): List<Tag>
}