package com.robivan.newsgb._core.ui.mapper

import com.robivan.newsgb._core.data.api.model.ArticleDTO
import com.robivan.newsgb._core.ui.model.Article
import com.robivan.newsgb.utils.ui.Category

object NewsDtoToUiMapper {
    operator fun invoke(
        newsList: List<ArticleDTO>,
        category: Category = Category.GENERAL
    ): List<Article> {
        return newsList.map { articleDTO ->
            Article(
                category = category,
                sourceName = articleDTO.source.name.orEmpty(),
                author = articleDTO.author.orEmpty(),
                title = articleDTO.title.orEmpty(),
                description = articleDTO.description.orEmpty(),
                contentUrl = articleDTO.contentUrl,
                imageUrl = articleDTO.imageUrl,
                publishedDate = articleDTO.publishedDate.orEmpty(),
                content = articleDTO.content.orEmpty()
            )
        }
    }
}