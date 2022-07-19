package com.example.newsgb.bookmarks.di

import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb.bookmarks.data.BookmarkRepositoryImpl
import com.example.newsgb.bookmarks.domain.BookmarkRepository
import com.example.newsgb.bookmarks.ui.BookmarksViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val bookmarkModule = module {
    single<BookmarkRepository> { BookmarkRepositoryImpl(bookmarkDB = get()) }
    viewModel { (store: NewsStore) -> BookmarksViewModel(bookmarkRepo = get(), store = store) }
}