package com.example.newsgb.article.di

import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb.article.domain.ArticleUseCases
import com.example.newsgb.article.ui.ArticleViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val articleModule = module {
    single { ArticleUseCases(bookmarkRepo = get() ) }
    viewModel { (store: NewsStore, articleUrl: String) ->
        ArticleViewModel(useCases = get(), store = store, articleUrl = articleUrl)
    }
}