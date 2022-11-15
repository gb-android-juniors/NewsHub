package com.robivan.newsgb._core.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.robivan.newsgb._core.ui.model.AdBanner
import com.robivan.newsgb._core.ui.model.Article
import com.robivan.newsgb._core.ui.model.NewsListItem
import com.robivan.newsgb.databinding.AdBannerRecyclerItemBinding
import com.robivan.newsgb.databinding.DefaultArticleRecyclerItemBinding
import com.robivan.newsgb.databinding.FirstArticleRecyclerItemBinding
import com.robivan.newsgb.utils.setBookmarkIconColor
import com.yandex.mobile.ads.banner.AdSize
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData

class NewsListAdapter(
    private val listener: RecyclerItemListener,
    private val isMainNewsScreen: Boolean = false
) :
    RecyclerView.Adapter<BaseViewHolder>() {

    private var adWidth: Int = 0

    private val newsListDiffer = AsyncListDiffer(this, ArticleDiffUtilCallback())

    fun submitList(list: List<NewsListItem>) = newsListDiffer.submitList(list)

    override fun getItemCount(): Int = newsListDiffer.currentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        adWidth = parent.width
        return when (viewType) {
            TYPE_FIRST_ARTICLE -> {
                val binding = FirstArticleRecyclerItemBinding.inflate(inflater, parent, false)
                FirstArticleViewHolder(binding)
            }
            TYPE_AD_BANNER -> {
                val binding = AdBannerRecyclerItemBinding.inflate(inflater, parent, false)
                AdBannerViewHolder(binding)
            }
            else -> {
                val binding = DefaultArticleRecyclerItemBinding.inflate(inflater, parent, false)
                DefaultArticleViewHolder(binding)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isMainNewsScreen && position == 0 -> TYPE_FIRST_ARTICLE
            isMainNewsScreen && (position + 1) % 11 == 0 -> TYPE_AD_BANNER
            else -> TYPE_DEFAULT_ARTICLE
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val newsListItem = newsListDiffer.currentList[position]
        when (getItemViewType(position)) {
            TYPE_FIRST_ARTICLE -> {
                (holder as FirstArticleViewHolder).bind(newsListItem = newsListItem)
            }
            TYPE_DEFAULT_ARTICLE -> {
                (holder as DefaultArticleViewHolder).bind(newsListItem = newsListItem)
            }
            TYPE_AD_BANNER -> {
                (holder as AdBannerViewHolder).bind(newsListItem = newsListItem)
            }
        }
    }

    inner class DefaultArticleViewHolder(
        private val binding: DefaultArticleRecyclerItemBinding
    ) : BaseViewHolder(binding.root) {

        override fun bind(newsListItem: NewsListItem) = with(binding) {
            newsListItem as Article
            newsHeader.text = newsListItem.title
            newsResourceName.text = newsListItem.sourceName
            newsItemBookmarkImage.apply {
                setBookmarkIconColor(
                    context = itemView.context,
                    bookmarkImage = this,
                    isChecked = newsListItem.isChecked
                )
                setOnClickListener { listener.onBookmarkCheck(newsListItem) }
            }
            Glide.with(newsImage.context)
                .load(newsListItem.imageUrl)
                .error(newsListItem.category.imgResId)
                .into(newsImage)
            itemView.setOnClickListener {
                listener.onItemClick(newsListItem)
            }
        }
    }

    inner class FirstArticleViewHolder(private val binding: FirstArticleRecyclerItemBinding) :
        BaseViewHolder(binding.root) {
        override fun bind(newsListItem: NewsListItem) = with(binding) {
            newsListItem as Article
            firstNewsHeader.text = newsListItem.title
            firstNewsSource.text = newsListItem.sourceName
            firstNewsBookmarkImage.apply {
                setBookmarkIconColor(
                    context = itemView.context,
                    bookmarkImage = this,
                    isChecked = newsListItem.isChecked
                )
                setOnClickListener { listener.onBookmarkCheck(newsListItem) }
            }
            Glide.with(firstNewsImage.context)
                .load(newsListItem.imageUrl)
                .error(newsListItem.category.imgResId)
                .into(firstNewsImage)

            itemView.setOnClickListener { listener.onItemClick(newsListItem) }
        }
    }

    inner class AdBannerViewHolder(private val binding: AdBannerRecyclerItemBinding) :
        BaseViewHolder(binding.root) {
        override fun bind(newsListItem: NewsListItem) {
            with(binding) {
                newsListItem as AdBanner
                val adContainer = adContainer
                adContainer.isVisible = false
                val adView = BannerAdView(root.context)
                adView.apply {
                    setAdSize(AdSize.flexibleSize(adWidth, AD_HEIGHT))
                    setAdUnitId(newsListItem.bannerId)
                    setBannerAdEventListener(object : BannerAdEventListener {
                        override fun onAdLoaded() {
                            adContainer.isVisible = true
                        }

                        override fun onAdFailedToLoad(error: AdRequestError) {
                            Log.d("TAG", error.description)
                            adContainer.isVisible = false
                        }

                        override fun onAdClicked() {}
                        override fun onLeftApplication() {}
                        override fun onReturnedToApplication() {}
                        override fun onImpression(p0: ImpressionData?) {}
                    })
                }
                adContainer.apply {
                    removeAllViews()
                    addView(adView)
                }
                val adRequest = AdRequest.Builder().build()
                adView.loadAd(adRequest)
            }
        }
    }

    companion object {
        const val TYPE_FIRST_ARTICLE = 0
        const val TYPE_DEFAULT_ARTICLE = 1
        const val TYPE_AD_BANNER = 2
        const val AD_HEIGHT = 200
    }
}
