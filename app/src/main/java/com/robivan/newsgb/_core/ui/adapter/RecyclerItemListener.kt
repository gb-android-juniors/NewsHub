package com.robivan.newsgb._core.ui.adapter

import com.robivan.newsgb._core.ui.model.Article

interface RecyclerItemListener {
    fun onItemClick(itemArticle: Article)
    fun onBookmarkCheck(itemArticle: Article)
}