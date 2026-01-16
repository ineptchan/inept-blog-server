package top.inept.blog.feature.tag.model.convert

import top.inept.blog.feature.tag.model.dto.CreateTagDTO
import top.inept.blog.feature.tag.model.entity.Tag
import top.inept.blog.feature.tag.model.vo.TagVO

fun Tag.toTagVO() = TagVO(
    id = this.id,
    name = this.name,
    slug = this.slug,
)

fun CreateTagDTO.toTag() = Tag(
    name = this.name,
    slug = this.slug,
)
