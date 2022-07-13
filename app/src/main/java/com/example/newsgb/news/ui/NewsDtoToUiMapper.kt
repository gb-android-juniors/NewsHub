package com.example.newsgb.news.ui

import com.example.newsgb._core.data.api.model.ArticleDTO
import com.example.newsgb._core.ui.model.Article

class NewsDtoToUiMapper {
    operator fun invoke(newsList: List<ArticleDTO>): List<Article> {
        return newsList.map { articleDTO ->
            Article(
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