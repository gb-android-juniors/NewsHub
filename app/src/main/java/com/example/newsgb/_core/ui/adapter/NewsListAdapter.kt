package com.example.newsgb._core.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsgb.R
import com.example.newsgb._core.ui.model.Article
import com.example.newsgb.databinding.NewsFragmentRecyclerItemBinding

class NewsListAdapter(private val listener: RecyclerItemListener) :
    RecyclerView.Adapter<NewsListAdapter.NewsViewHolder>() {

    private val newsListDiffer = AsyncListDiffer(this, DIFF_CALLBACK)

    fun submitList(list: List<Article>) = newsListDiffer.submitList(list)

    override fun getItemCount(): Int = newsListDiffer.currentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = NewsFragmentRecyclerItemBinding.inflate(inflater, parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = newsListDiffer.currentList[position]
        holder.bind(itemArticle = article)
    }

    inner class NewsViewHolder(
        private val binding: NewsFragmentRecyclerItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(itemArticle: Article) = with(binding) {
            newsHeader.text = itemArticle.title
            newsResourceName.text = itemArticle.sourceName
            Glide.with(newsImage.context)
                .load(itemArticle.imageUrl)
                .placeholder(R.drawable.ic_newspaper_24)
                .error(R.drawable.news)
                .into(newsImage)
            itemView.setOnClickListener {
                listener.onItemClick(itemArticle)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.contentUrl == newItem.contentUrl
            }

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem == newItem
            }
        }
    }
}
