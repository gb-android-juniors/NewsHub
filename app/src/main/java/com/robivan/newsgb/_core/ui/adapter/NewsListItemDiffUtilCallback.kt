package com.robivan.newsgb._core.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.robivan.newsgb._core.ui.model.AdBanner
import com.robivan.newsgb._core.ui.model.Article
import com.robivan.newsgb._core.ui.model.NewsListItem

class NewsListItemDiffUtilCallback : DiffUtil.ItemCallback<NewsListItem>() {
    override fun areItemsTheSame(oldItem: NewsListItem, newItem: NewsListItem): Boolean {
        return when {
            oldItem is Article && newItem is Article -> oldItem.isTheSame(newItem)
            oldItem is AdBanner && newItem is AdBanner -> oldItem.id == newItem.id
            else -> false
        }


    }

    override fun areContentsTheSame(oldItem: NewsListItem, newItem: NewsListItem): Boolean {
        return when {
            oldItem is Article && newItem is Article -> oldItem as Article == newItem as Article
            oldItem is AdBanner && newItem is AdBanner -> oldItem as AdBanner == newItem as AdBanner
            else -> false
        }
    }
}