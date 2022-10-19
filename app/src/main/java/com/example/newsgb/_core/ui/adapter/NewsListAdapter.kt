package com.example.newsgb._core.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsgb._core.ui.model.Article
import com.example.newsgb.databinding.DefaultArticleRecyclerItemBinding
import com.example.newsgb.databinding.FirstArticleRecyclerItemBinding
import com.example.newsgb.utils.setBookmarkIconColor

class NewsListAdapter(
    private val listener: RecyclerItemListener,
    private val isMainNewsScreen: Boolean = false
) :
    RecyclerView.Adapter<BaseViewHolder>() {

    private val newsListDiffer = AsyncListDiffer(this, ArticleDiffUtilCallback())

    fun submitList(list: List<Article>) = newsListDiffer.submitList(list)

    override fun getItemCount(): Int = newsListDiffer.currentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_FIRST_ARTICLE -> {
                val binding = FirstArticleRecyclerItemBinding.inflate(inflater, parent, false)
                FirstArticleViewHolder(binding)
            }
            else -> {
                val binding = DefaultArticleRecyclerItemBinding.inflate(inflater, parent, false)
                DefaultArticleViewHolder(binding)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isMainNewsScreen && position == 0) {
            TYPE_FIRST_ARTICLE
        } else {
            TYPE_DEFAULT_ARTICLE
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val article = newsListDiffer.currentList[position]
        when (getItemViewType(position)) {
            TYPE_FIRST_ARTICLE -> {
                (holder as FirstArticleViewHolder).bind(itemArticle = article)
            }
            else -> {
                (holder as DefaultArticleViewHolder).bind(itemArticle = article)
            }
        }
    }

    inner class DefaultArticleViewHolder(
        private val binding: DefaultArticleRecyclerItemBinding
    ) : BaseViewHolder(binding.root) {

        override fun bind(itemArticle: Article) = with(binding) {
            newsHeader.text = itemArticle.title
            newsResourceName.text = itemArticle.sourceName
            newsItemBookmarkImage.apply {
                setBookmarkIconColor(
                    context = itemView.context,
                    bookmarkImage = this,
                    isChecked = itemArticle.isChecked
                )
                setOnClickListener { listener.onBookmarkCheck(itemArticle) }
            }
            Glide.with(newsImage.context)
                .load(itemArticle.imageUrl)
                .error(itemArticle.category.imgResId)
                .into(newsImage)
            itemView.setOnClickListener {
                listener.onItemClick(itemArticle)
            }
        }
    }

    inner class FirstArticleViewHolder(private val binding: FirstArticleRecyclerItemBinding) :
        BaseViewHolder(binding.root) {
        override fun bind(itemArticle: Article) = with(binding) {
            firstNewsHeader.text = itemArticle.title
            firstNewsSource.text = itemArticle.sourceName
            firstNewsBookmarkImage.apply {
                setBookmarkIconColor(
                    context = itemView.context,
                    bookmarkImage = this,
                    isChecked = itemArticle.isChecked
                )
                setOnClickListener { listener.onBookmarkCheck(itemArticle) }
            }
            Glide.with(firstNewsImage.context)
                .load(itemArticle.imageUrl)
                .error(itemArticle.category.imgResId)
                .into(firstNewsImage)

            itemView.setOnClickListener { listener.onItemClick(itemArticle) }
        }
    }

    companion object {
        const val TYPE_FIRST_ARTICLE = 0
        const val TYPE_DEFAULT_ARTICLE = 1
    }
}
