package com.robivan.newsgb._core.ui.mapper

import com.robivan.newsgb._core.data.db.entity.ArticleEntity
import com.robivan.newsgb._core.ui.model.Article
import com.robivan.newsgb.utils.ui.Category

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