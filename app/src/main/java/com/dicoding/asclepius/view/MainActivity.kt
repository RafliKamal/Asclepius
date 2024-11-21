package com.dicoding.asclepius.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.helper.ImageClassifierHelper.ClassifierListener
import com.dicoding.asclepius.view.articles.ArticlesActivity
import com.dicoding.asclepius.view.history.HistoryActivity
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File

class MainActivity : AppCompatActivity(), ClassifierListener {

    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    companion object {
        private const val REQUEST_PERMISSION = 100
        private const val KEY_IMAGE_URI = "key_image_uri"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState != null) {
            currentImageUri = savedInstanceState.getParcelable(KEY_IMAGE_URI)
            showImage()
        }

        imageClassifierHelper = ImageClassifierHelper(this, this)

        binding.apply {
            galleryButton.setOnClickListener { checkGalleryPermission() }
            analyzeButton.setOnClickListener { analyzeImage() }
            historyButton.setOnClickListener { openHistory() }
            infoButton.setOnClickListener {
                val intent = Intent(this@MainActivity, ArticlesActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun checkGalleryPermission() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (permissions.all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }) {
            startGallery()
        } else {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION)
        }
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
        }
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                openCropActivity(uri)
            } ?: showToast("Error getting selected file")
        } else {
            showToast("No image selected")
        }
    }

    private fun openCropActivity(uri: Uri) {
        val destinationUri = Uri.fromFile(File(cacheDir, "cropped_${System.currentTimeMillis()}.jpg"))
        val uCrop = UCrop.of(uri, destinationUri)
        uCrop.withAspectRatio(1f, 1f)
        uCrop.withMaxResultSize(1080, 1080)
        uCropLauncher.launch(uCrop.getIntent(this))
    }

    private val uCropLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resultUri = UCrop.getOutput(result.data!!)
            resultUri?.let {
                currentImageUri = it
                showImage()
            }
        } else {
            showToast("Image cropping failed")
        }
    }

    private fun showImage() {
        currentImageUri?.let { uri ->
            try {
                val inputStream = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                binding.apply {
                    previewImageView.setImageBitmap(bitmap)
                    analyzeButton.isEnabled = true
                }
            } catch (e: Exception) {
                showToast("Failed to load image")
            }
        } ?: showToast("Failed to load image")
    }

    private fun analyzeImage() {
        currentImageUri?.let {
            imageClassifierHelper.classifyStaticImage(it)
        } ?: showToast("Please select an image first")
    }

    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
        if (results.isNullOrEmpty()) {
            showToast("No results found")
            return
        }

        val topResult = results[0].categories[0]
        val resultText = topResult.label
        val resultScore = "${"%.0f".format(topResult.score * 100)}%"
        moveToResult(currentImageUri, resultText, resultScore)
    }

    override fun onError(error: String) {
        showToast(error)
    }

    private fun moveToResult(resultImageUri: Uri?, resultText: String, resultScore: String) {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("RESULT_IMAGE_URI", resultImageUri.toString())
            putExtra("RESULT_TEXT", resultText)
            putExtra("RESULT_SCORE", resultScore)
        }
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun openHistory() {
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startGallery()
        } else {
            showToast("Permission denied")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_IMAGE_URI, currentImageUri)
    }
}
