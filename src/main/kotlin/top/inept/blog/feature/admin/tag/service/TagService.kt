package top.inept.blog.feature.admin.tag.service

import top.inept.blog.feature.admin.tag.pojo.entity.Tag

interface TagService {
    fun getTags(): List<Tag>
    fun getTagById(id: Long): Tag
    fun createTag(tag: Tag): Tag
    fun updateTag(tag: Tag): Tag
    fun deleteTag(id: Long)
}