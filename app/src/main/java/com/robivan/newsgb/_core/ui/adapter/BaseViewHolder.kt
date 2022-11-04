package com.robivan.newsgb._core.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.robivan.newsgb._core.ui.model.Article

abstract class BaseViewHolder(viewItem: View) : RecyclerView.ViewHolder(viewItem)  {
    abstract fun bind(itemArticle: Article)
}