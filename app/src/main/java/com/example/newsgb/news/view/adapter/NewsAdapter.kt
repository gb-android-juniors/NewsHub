package com.example.newsgb.news.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsgb.R
import com.example.newsgb._core.data.db.entity.ArticleEntity
import com.example.newsgb.databinding.NewsFragmentRecyclerItemBinding

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private var onItemClickListener: ((ArticleEntity) -> Unit)? = null

    private val diffUtilCallback = object : DiffUtil.ItemCallback<ArticleEntity>() {
        override fun areItemsTheSame(oldItem: ArticleEntity, newItem: ArticleEntity): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: ArticleEntity, newItem: ArticleEntity): Boolean {
            return oldItem == newItem
        }
    }

    val newsListDiffer = AsyncListDiffer(this, diffUtilCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = NewsFragmentRecyclerItemBinding.inflate(inflater, parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = newsListDiffer.currentList[position]
        with(holder.binding) {
            newsHeader.text = article.title
            newsResourceName.text = article.source.name
            Glide.with(newsImage.context)
                .load(article.urlToImage)
                .placeholder(R.drawable.ic_newspaper_24)
                .error(R.drawable.ic_newspaper_24)
                .into(newsImage)
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.let {
                it(article)
            }
        }
    }

    override fun getItemCount(): Int = newsListDiffer.currentList.size

    fun setOnItemClickListener(Listener: (ArticleEntity) -> Unit) {
        onItemClickListener = Listener
    }

    class NewsViewHolder(
        val binding: NewsFragmentRecyclerItemBinding,
    ) : RecyclerView.ViewHolder(binding.root)
}
