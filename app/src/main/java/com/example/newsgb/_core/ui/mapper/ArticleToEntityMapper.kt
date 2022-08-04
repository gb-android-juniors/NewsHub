package com.example.newsgb._core.ui.mapper

import com.example.newsgb._core.data.db.entity.ArticleEntity
import com.example.newsgb._core.ui.model.Article
import com.example.newsgb.utils.ui.Category

object ArticleToEntityMapper {
    operator fun invoke(
        article: Article
    ): ArticleEntity {
        return ArticleEntity(
            sourceName = article.sourceName,
            author = article.author,
            title = article.title,
            description = article.description,
            contentUrl = article.contentUrl,
            urlToImage = article.imageUrl,
            publishedDate = article.publishedDate,
            content = article.content,
            category = Category.BOOKMARKS
        )
    }
}