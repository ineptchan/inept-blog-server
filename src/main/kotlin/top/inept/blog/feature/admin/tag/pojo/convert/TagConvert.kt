package top.inept.blog.feature.admin.tag.pojo.convert

import top.inept.blog.feature.admin.tag.pojo.dto.CreateTagDTO
import top.inept.blog.feature.admin.tag.pojo.dto.UpdateTagDTO
import top.inept.blog.feature.admin.tag.pojo.entity.Tag
import top.inept.blog.feature.admin.tag.pojo.vo.TagVO

fun Tag.toTagVO() = TagVO(
    id = this.id,
    name = this.name,
    slug = this.slug,
)

fun UpdateTagDTO.toTag() = Tag(
    id = this.id,
    name = this.name,
    slug = this.slug,
)

fun CreateTagDTO.toTag() = Tag(
    name = this.name,
    slug = this.slug,
)
