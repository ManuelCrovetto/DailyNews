package com.macrosystems.dailynews.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.macrosystems.dailynews.R
import com.macrosystems.dailynews.data.model.news.NewsResponse
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.FragmentComponent
import javax.inject.Inject
import javax.inject.Singleton

class NewsFeedAdapter @Inject constructor(private val glide: RequestManager): RecyclerView.Adapter<NewsFeedViewHolder>() {

    private var newsFeed: MutableList<NewsResponse> = mutableListOf()

    var onClickListener: (NewsResponse) -> Unit = {
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: MutableList<NewsResponse>) {
        if (data.size < 0) data.removeAt(0)
        newsFeed = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsFeedViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        return NewsFeedViewHolder(itemView = itemView, glide = glide)
    }

    override fun onBindViewHolder(holder: NewsFeedViewHolder, position: Int) {
        holder.onBind(newsFeed[position], onClickListener)
    }

    override fun getItemCount(): Int = newsFeed.size
}