package com.example.newsappfirebase.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsappfirebase.R
import com.example.newsappfirebase.databinding.ItemNewsBinding
import com.example.newsappfirebase.model.NewsModel

class NewsPagingAdapter(
    private val onItemClick: ((NewsModel) -> Unit)? = null,
    private val addOrRemoveFavoriteListener: ((NewsModel, Boolean) -> Unit)? = null
) :
    PagingDataAdapter<NewsModel, NewsPagingAdapter.NewsViewHolder>(NewsDiffCallback()) {

    class NewsViewHolder(
        private val binding: ItemNewsBinding,
        private val onItemClick: ((NewsModel) -> Unit)? = null,
        private val addOrRemoveFavoriteListener: ((NewsModel, Boolean) -> Unit)? = null
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(news: NewsModel?) {
            news?.let {
                binding.apply {
                    tvNewsTitle.text = it.name
                    tvTitle.text = it.source
                    tvDescription.text = it.description
                    loadImage(it, imgNews)
                    itemView.setOnClickListener {
                        onItemClick?.let { it1 -> it1(news) }
                    }

                    if (it.isFavorite == true) {
                        btnFav.setImageResource(R.drawable.ic_mark_checked)
                    } else {
                        btnFav.setImageResource(R.drawable.ic_mark_unchecked)
                    }

                    btnFav.setOnClickListener {
                        val newFavoriteStatus = !(news.isFavorite ?: false)
                        if (newFavoriteStatus) {
                            btnFav.setImageResource(R.drawable.ic_mark_checked)
                        } else {
                            btnFav.setImageResource(R.drawable.ic_mark_unchecked)
                        }
                        addOrRemoveFavoriteListener?.let { it1 -> it1(news, newFavoriteStatus) }
                    }
                }
            }
        }

        private fun loadImage(newsItem: NewsModel, imageView: ImageView) {
            if (!newsItem.image.isNullOrEmpty()) {
                Glide.with(imageView.context)
                    .load(newsItem.image)
                    .placeholder(R.drawable.news)
                    .into(imageView)
            } else {
                imageView.setImageResource(R.drawable.news)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NewsViewHolder(binding, onItemClick, addOrRemoveFavoriteListener)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = getItem(position)
        holder.bind(news)
    }

    class NewsDiffCallback : DiffUtil.ItemCallback<NewsModel>() {
        override fun areItemsTheSame(oldItem: NewsModel, newItem: NewsModel): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: NewsModel, newItem: NewsModel): Boolean {
            return oldItem == newItem
        }

    }
}