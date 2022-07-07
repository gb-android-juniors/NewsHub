package com.example.newsgb.article.domain

interface ArticleRepository {
    fun <T> getArticleFromSource(url: String): Result<T>
}