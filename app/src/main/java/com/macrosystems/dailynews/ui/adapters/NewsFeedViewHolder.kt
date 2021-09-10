package com.macrosystems.dailynews.ui.adapters


import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.macrosystems.dailynews.R
import com.macrosystems.dailynews.core.ex.formatDate
import com.macrosystems.dailynews.data.model.constants.Constants.IMG_HTML_TAG
import com.macrosystems.dailynews.data.model.constants.Constants.SRC_HTML_TAG
import com.macrosystems.dailynews.data.model.news.NewsResponse
import com.macrosystems.dailynews.databinding.NewsItemBinding
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class NewsFeedViewHolder (itemView: View, private val glide: RequestManager): RecyclerView.ViewHolder(itemView) {

    private val binding = NewsItemBinding.bind(itemView)
    private val context = binding.root.context

    fun onBind(news: NewsResponse, onClickListener: (NewsResponse) -> Unit) {
        setUpNewsImage(news.content.rendered)
        setUpCopies(news)
        itemView.setOnClickListener { onClickListener(news) }
    }

    private fun setUpNewsImage(content: String) {
        val html: Document = Jsoup.parse(content)
        val imageUrl = html.select(IMG_HTML_TAG).first()

        glide.load(imageUrl.attr(SRC_HTML_TAG) ?: AppCompatResources.getDrawable(context, R.drawable.fall_back_image)).into(binding.ivNewsImage)
    }

    private fun setUpCopies(news: NewsResponse) {
        with(binding){
            tvNewsTitle.text = news.title.rendered
            tvNewsDate.text = news.date.formatDate().orEmpty()
        }
    }

}