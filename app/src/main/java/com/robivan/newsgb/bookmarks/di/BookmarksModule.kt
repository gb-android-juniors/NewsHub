package com.robivan.newsgb.bookmarks.di

import com.robivan.newsgb.bookmarks.data.BookmarkRepositoryImpl
import com.robivan.newsgb.bookmarks.domain.BookmarkRepository
import com.robivan.newsgb.bookmarks.domain.BookmarksUseCases
import com.robivan.newsgb.bookmarks.ui.BookmarksViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val bookmarkModule = module {
    single<BookmarkRepository> { BookmarkRepositoryImpl(bookmarkDB = get()) }
    single { BookmarksUseCases(bookmarkRepo = get()) }
    viewModel { BookmarksViewModel(useCases = get(), store = get()) }
}