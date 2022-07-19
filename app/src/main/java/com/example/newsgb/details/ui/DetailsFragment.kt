package com.example.newsgb.details.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.example.newsgb.R
import com.example.newsgb._core.ui.model.Article
import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb._core.ui.store.NewsStoreHolder
import com.example.newsgb.article.ui.ArticleFragment
import com.example.newsgb.databinding.DetailsFragmentBinding
import com.example.newsgb.utils.Constants.Companion.ARTICLE

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class DetailsFragment : Fragment() {
    /** переменная хранителя экземпляра NewsStore */
    private var storeHolder: NewsStoreHolder? = null

    /** экземпляр NewsStore, который получаем из MainActivity как хранителя этого экземпляра */
    private val newsStore: NewsStore by lazy {
        storeHolder?.newsStore ?: throw IllegalArgumentException()
    }

    private var _binding: DetailsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        /** инициализируем переменную хранителя экземпляра NewsStore */
        storeHolder = context as NewsStoreHolder
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DetailsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parseArticle()
    }

    private fun parseArticle() {
        val article = arguments?.getParcelable<Article>(ARTICLE)
        binding.articleHeaderText.text = article?.title
        binding.articleSourceName.text = article?.sourceName
        binding.publicationDate.text = article?.publishedDate
        binding.selectedNewsDescriptionText.text = article?.description
        binding.articleSourceName.setOnClickListener {
            showArticleFragment(ArticleFragment.newInstance(article!!.contentUrl))
        }
        Glide.with(binding.articleImage)
            .load(article?.imageUrl)
            .placeholder(R.drawable.ic_newspaper_24)
            .error(R.drawable.ic_newspaper_24)
            .into(binding.articleImage)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showArticleFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        @JvmStatic
        fun newInstance(article: Article) =
            DetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARTICLE, article)
                }
            }
    }
}