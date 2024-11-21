package com.dicoding.asclepius.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.util.Log
import com.dicoding.asclepius.R
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier

class ImageClassifierHelper(
    private val context: Context,
    private val classifierListener: ClassifierListener?,
    private val threshold: Float = 0.1f,
    private val maxResults: Int = 3,
    private val modelName: String = "cancer_classification.tflite"
) {
    private var imageClassifier: ImageClassifier? = null

    init {
        setupImageClassifier()
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(results: List<Classifications>?, inferenceTime: Long)
    }

    private fun setupImageClassifier() {
        val options = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResults)
            .setBaseOptions(BaseOptions.builder().setNumThreads(4).build())
            .build()

        try {
            imageClassifier = ImageClassifier.createFromFileAndOptions(context, modelName, options)
        } catch (e: IllegalStateException) {
            classifierListener?.onError(context.getString(R.string.image_classifier_failed))
            Log.e(TAG, e.message.toString())
        }
    }

    fun classifyStaticImage(imageUri: Uri) {
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .build()

        val bitmap = getBitmapFromUri(imageUri) ?: run {
            classifierListener?.onError("Failed to decode the image.")
            return
        }

        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))

        val inferenceTime = SystemClock.uptimeMillis()
        val results = imageClassifier?.classify(tensorImage)
        classifierListener?.onResults(results, SystemClock.uptimeMillis() - inferenceTime)
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
        }?.copy(Bitmap.Config.ARGB_8888, true)
    }

    companion object {
        private const val TAG = "ImageClassifierHelper"
    }
}
