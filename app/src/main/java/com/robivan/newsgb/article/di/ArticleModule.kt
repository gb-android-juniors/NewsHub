package com.robivan.newsgb.article.di

import com.robivan.newsgb._core.ui.model.Article
import com.robivan.newsgb.article.domain.ArticleUseCases
import com.robivan.newsgb.article.ui.ArticleViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val articleModule = module {
    single { ArticleUseCases(bookmarkRepo = get() ) }
    viewModel { (article: Article) ->
        ArticleViewModel(useCases = get(), store = get(), article = article)
    }
}