package top.inept.blog.feature.tag.service


import org.springframework.data.domain.Page
import top.inept.blog.feature.tag.pojo.dto.CreateTagDTO
import top.inept.blog.feature.tag.pojo.dto.QueryTagDTO
import top.inept.blog.feature.tag.pojo.dto.UpdateTagDTO
import top.inept.blog.feature.tag.pojo.entity.Tag

interface TagService {
    fun getTags(queryTagDTO: QueryTagDTO): Page<Tag>
    fun getTagById(id: Long): Tag
    fun createTag(createTagDTO: CreateTagDTO): Tag
    fun updateTag(updateTagDTO: UpdateTagDTO): Tag
    fun deleteTag(id: Long)
    fun getTagsByIds(ids: List<Long>): List<Tag>
}