package com.example.newsgb._core.ui


import com.example.newsgb._core.data.db.entity.ArticleEntity
import com.example.newsgb._core.ui.model.Article
import com.example.newsgb.utils.ui.Category

object EntitiesToArticleMapper {
    operator fun invoke(
        entitiesList: List<ArticleEntity>
    ): List<Article> {
        return entitiesList.map { articleEntity ->
            mapEntityToArticle(articleEntity)
        }
    }

    private fun mapEntityToArticle(
        articleEntity: ArticleEntity
    ): Article {
        return Article(
            category = Category.BOOKMARKS,
            sourceName = articleEntity.sourceName,
            author = articleEntity.author.orEmpty(),
            title = articleEntity.title,
            description = articleEntity.description.orEmpty(),
            contentUrl = articleEntity.contentUrl,
            imageUrl = articleEntity.urlToImage,
            publishedDate = articleEntity.publishedDate,
            content = articleEntity.content.orEmpty(),
            isChecked = true
        )
    }
}

