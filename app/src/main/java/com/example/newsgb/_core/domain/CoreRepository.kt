package com.example.newsgb._core.domain

interface CoreRepository {
    fun <T> addToBookmarks(article: T): Result<Boolean>
    fun removeFromBookmarks(articleId: Long): Result<Boolean>
}