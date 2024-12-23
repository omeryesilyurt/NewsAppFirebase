package com.example.newsappfirebase.ui.favorites

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsappfirebase.R
import com.example.newsappfirebase.databinding.ItemNewsBinding
import com.example.newsappfirebase.model.NewsModel
import javax.inject.Inject

class FavoritesAdapter @Inject constructor(
    private val addOrRemoveFavoriteListener: (news: NewsModel, isFavorite: Boolean) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {
    private var newsList: List<NewsModel> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoritesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        val newsItem = newsList[position]
        holder.bind(newsItem)
        holder.apply { }
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    inner class FavoritesViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(news: NewsModel) {
            binding.apply {
                tvNewsTitle.text = news.name
                tvTitle.text = news.source
                tvDescription.text = news.description
                loadImage(news, imgNews)

                if (news.isFavorite == true) {
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
                    addOrRemoveFavoriteListener(news, newFavoriteStatus)
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

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(news: List<NewsModel>) {
        this.newsList = news
        notifyDataSetChanged()
    }
}
