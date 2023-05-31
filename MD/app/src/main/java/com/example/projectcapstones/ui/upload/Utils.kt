package com.example.projectcapstones.ui.upload

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import com.example.projectcapstones.R
import org.tensorflow.lite.Interpreter
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*

private const val FILENAME_FORMAT = "dd-MMM-yyyy"

val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())

fun createCustomTempFile(context: Context): File =
    File.createTempFile(timeStamp, ".jpg", context.getExternalFilesDir(Environment.DIRECTORY_PICTURES))

fun createFile(application: Application): File {
    val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
        File(it, application.resources.getString(R.string.app_name)).apply { mkdirs() }
    }
    val outputDirectory = mediaDir ?: application.filesDir
    return File(outputDirectory, "$timeStamp.jpg")
}

fun rotateFile(file: File, isBackCamera: Boolean = false) {
    val matrix = Matrix().apply {
        postRotate(if (isBackCamera) 90f else -90f)
        if (!isBackCamera) {
            val bitmap = BitmapFactory.decodeFile(file.path)
            postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
        }
    }
    val bitmap = BitmapFactory.decodeFile(file.path)
    val result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    result.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))
}


fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createCustomTempFile(context)
    contentResolver.openInputStream(selectedImg)?.use { inputStream ->
        FileOutputStream(myFile).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }
    return myFile
}

class ClassifierSkin(
    assetManager: AssetManager,
    modelPath: String,
    labelPath: String,
) {
    @Suppress("DEPRECATION")
    private val interpreter: Interpreter = Interpreter(loadModelTF(assetManager, modelPath))
    private val labelList: List<String> = resultLabelTF(assetManager, labelPath)
    private val pixelSize: Int = 3
    private val imageMean = 0
    private val inputSize: Int =  224
    private val imageStd = 255.0f
    private val maxResults = 3
    private val threshold = 0.4f

    data class Recognition(
        var id: String = "",
        var title: String = "",
        var confidence: Float = 0F
    ) {
        override fun toString(): String = "Title = $title, Confidence = $confidence)"
    }

    private fun loadModelTF(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        return inputStream.channel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength)
    }

    private fun resultLabelTF(assetManager: AssetManager, labelPath: String): List<String> =
        assetManager.open(labelPath).bufferedReader().useLines { it.toList() }

    fun scanImage(bitmap: Bitmap): List<Recognition> {
        val scaledBitmap = scaleImage(bitmap)
        val byteBuffer = convertBitmapToByteBuffer(scaledBitmap)
        val result = Array(1) { FloatArray(labelList.size) }
        interpreter.run(byteBuffer, result)
        return getSortedResult(result)
    }

    private fun scaleImage(bitmap: Bitmap): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, false)
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * pixelSize).order(ByteOrder.nativeOrder())
        val intValues = IntArray(inputSize * inputSize)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        var pixel = 0
        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val value = intValues[pixel++]
                byteBuffer.putFloat((((value shr 16) and 0xFF) - imageMean) / imageStd)
                byteBuffer.putFloat((((value shr 8) and 0xFF) - imageMean) / imageStd)
                byteBuffer.putFloat((((value and 0xFF) - imageMean) / imageStd))
            }
        }
        return byteBuffer
    }

    private fun getSortedResult(labelProbArray: Array<FloatArray>): List<Recognition> {
        val pq = PriorityQueue<Recognition>(
            maxResults,
            compareBy { -it.confidence }
        )
        for (i in labelList.indices) {
            val confidence = labelProbArray[0][i]
            if (confidence >= threshold) {
                pq.add(Recognition("" + i, labelList.getOrNull(i) ?: "Unknown", confidence))
            }
        }
        val recognitions = ArrayList<Recognition>()
        val recognitionsSize = minOf(pq.size, maxResults)
        repeat(recognitionsSize) {
            pq.poll()?.let { recognitions.add(it) }
        }
        return recognitions
    }
}
