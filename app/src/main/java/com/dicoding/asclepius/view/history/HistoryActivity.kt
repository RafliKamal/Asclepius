package com.dicoding.asclepius.view.history

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.databinding.ActivityHistoryBinding
import com.dicoding.asclepius.view.ResultActivity

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private val historyViewModel: HistoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "History"
        }

        binding.rvHistory.layoutManager = LinearLayoutManager(this)

        historyViewModel.allHistory.observe(this) { historyList ->
            if (historyList.isEmpty()) {
                binding.tvEmptyMessage.visibility = View.VISIBLE
                binding.rvHistory.visibility = View.GONE
            } else {
                binding.tvEmptyMessage.visibility = View.GONE
                binding.rvHistory.visibility = View.VISIBLE
                binding.rvHistory.adapter = HistoryAdapter(historyList) { historyItem ->
                    val intent = Intent(this, ResultActivity::class.java).apply {
                        putExtra("IS_FROM_HISTORY", true)
                        putExtra("HISTORY_ITEM", historyItem)
                        putExtra("RESULT_IMAGE_URI", historyItem.image)
                        putExtra("RESULT_TEXT", historyItem.prediksi)
                        putExtra("RESULT_SCORE", historyItem.score)
                    }
                    startActivity(intent)
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
