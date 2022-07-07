package com.example.newsgb.bookmarks.domain

interface BookmarkRepository {
    fun <T> getAllBookmarks(): Result<T>
    fun clearBookmarks(): Result<Boolean>

}