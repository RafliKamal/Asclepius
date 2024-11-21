package com.dicoding.asclepius.view.articles

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.response.ArticlesItem
import com.dicoding.asclepius.databinding.ItemRowArticlesBinding

class ArticlesAdapter(private val articles: List<ArticlesItem>) : RecyclerView.Adapter<ArticlesAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(private val binding: ItemRowArticlesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: ArticlesItem) {
            binding.apply {
                tvItemTitle.text = article.title
                tvItemDescription.text = article.description
                Glide.with(itemView.context)
                    .load(article.urlToImage)
                    .placeholder(R.drawable.ic_place_holder)
                    .into(imgItemPhoto)

                root.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemRowArticlesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    override fun getItemCount(): Int = articles.size
}
