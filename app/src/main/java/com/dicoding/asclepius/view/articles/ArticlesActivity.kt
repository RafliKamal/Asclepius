package com.dicoding.asclepius.view.articles

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.databinding.ActivityArticlesBinding

class ArticlesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticlesBinding
    private val viewModel: ArticlesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticlesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Articles"
        }

        setupRecyclerView()
        observeArticles()

        val apiKey = "d8b7ac86f95b4b549efb65f4773fde20"
        viewModel.fetchArticles(apiKey)
    }

    private fun setupRecyclerView() {
        binding.rvArticles.layoutManager = LinearLayoutManager(this)
    }

    private fun observeArticles() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            binding.tvErrorMessage.text = errorMessage
            binding.tvErrorMessage.visibility = if (errorMessage.isNotEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.articles.observe(this) { articles ->
            if (articles != null) {
                viewModel.articles.observe(this) { articles ->
                    if (articles != null) {
                        binding.rvArticles.adapter = ArticlesAdapter(articles)
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
