package com.example.newsgb._core.ui

import com.example.newsgb._core.data.api.model.ArticleDTO
import com.example.newsgb._core.ui.model.Article
import com.example.newsgb.utils.ui.Category

object NewsDtoToUiMapper {
    operator fun invoke(
        newsList: List<ArticleDTO>,
        category: Category = Category.GENERAL
    ): List<Article> {
        return newsList.map { articleDTO ->
            Article(
                category = category,
                sourceName = articleDTO.source.name,
                author = articleDTO.author.orEmpty(),
                title = articleDTO.title,
                description = articleDTO.description.orEmpty(),
                contentUrl = articleDTO.contentUrl,
                imageUrl = articleDTO.imageUrl,
                publishedDate = articleDTO.publishedDate,
                content = articleDTO.content.orEmpty()
            )
        }
    }
}