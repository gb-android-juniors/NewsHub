package com.example.newsgb.bookmarks.di

import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb.bookmarks.data.BookmarkRepositoryImpl
import com.example.newsgb.bookmarks.domain.BookmarkRepository
import com.example.newsgb.bookmarks.domain.BookmarksUseCases
import com.example.newsgb.bookmarks.ui.BookmarksViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val bookmarkModule = module {
    single<BookmarkRepository> { BookmarkRepositoryImpl(bookmarkDB = get()) }
    single { BookmarksUseCases(bookmarkRepo = get()) }
    viewModel { (store: NewsStore) -> BookmarksViewModel(useCases = get(), store = store) }
}