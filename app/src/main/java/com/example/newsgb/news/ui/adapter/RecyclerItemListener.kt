package com.example.newsgb.news.ui.adapter

import com.example.newsgb._core.ui.model.Article

interface RecyclerItemListener {
    fun onItemClick(itemArticle: Article)
    fun onBookmarkCheck()
}