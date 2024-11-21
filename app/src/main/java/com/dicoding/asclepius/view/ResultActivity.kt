package com.dicoding.asclepius.view

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.database.History
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.view.history.HistoryViewModel

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private val historyViewModel: HistoryViewModel by viewModels()
    private var currentHistory: History? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Analyze Result"
        }

        val resultImageUriString = intent.getStringExtra("RESULT_IMAGE_URI")
        val resultImageUri = resultImageUriString?.let { Uri.parse(it) }
        val resultText = intent.getStringExtra("RESULT_TEXT")
        val resultScore = intent.getStringExtra("RESULT_SCORE")

        if (resultImageUri != null) {
            Glide.with(this)
                .load(resultImageUri)
                .placeholder(R.drawable.ic_place_holder)
                .error(R.drawable.ic_place_holder)
                .into(binding.resultImage)
        } else {
            binding.resultImage.setImageResource(R.drawable.ic_place_holder)
        }

        binding.resultText.text = resultText ?: getString(R.string.no_result_available)
        binding.resultScore.text = resultScore ?: getString(R.string.no_result_available)

        val isFromHistory = intent.getBooleanExtra("IS_FROM_HISTORY", false)
        currentHistory = intent.getParcelableExtra("HISTORY_ITEM")

        if (isFromHistory) {
            binding.saveButton.visibility = View.GONE
            binding.deleteButton.visibility = View.VISIBLE
        } else {
            binding.saveButton.visibility = View.VISIBLE
            binding.deleteButton.visibility = View.GONE
        }

        binding.saveButton.setOnClickListener {
            saveHistory(resultImageUri, resultText, resultScore)
        }

        binding.deleteButton.setOnClickListener {
            currentHistory?.let { history ->
                historyViewModel.delete(history)
                Toast.makeText(this, "History deleted", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun saveHistory(imageUri: Uri?, prediksi: String?, score: String?) {
        val history = History(
            image = imageUri.toString(),
            prediksi = prediksi,
            score = score
        )
        historyViewModel.insert(history)
        Toast.makeText(this, "History saved", Toast.LENGTH_SHORT).show()
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
