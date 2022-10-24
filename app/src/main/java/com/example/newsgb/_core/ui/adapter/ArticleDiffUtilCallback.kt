package com.example.newsgb._core.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.newsgb._core.ui.model.Article

class ArticleDiffUtilCallback : DiffUtil.ItemCallback<Article>(){
    override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem.isTheSame(newItem)
    }

    override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem == newItem
    }
}