package com.example.projectcapstones.ui.configcamera

import android.app.Application
import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import androidx.exifinterface.media.ExifInterface
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

fun createFile(application: Application): File =
    File(application.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: application.filesDir, "$timeStamp.jpg")

fun rotateFile(file: File, isBackCamera: Boolean = false) {
    val matrix = Matrix().apply {
        postRotate(if (isBackCamera) 90f else -90f)
        if (!isBackCamera) {
            BitmapFactory.decodeFile(file.path)?.run {
                postScale(-1f, 1f, width / 2f, height / 2f)
            }
        }
    }
    BitmapFactory.decodeFile(file.path)?.let { bitmap ->
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)?.let { result ->
            FileOutputStream(file).use { outputStream ->
                result.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
        }
    }
}

fun uriToFile(selectedImg: Uri, context: Context): File {
    val myFile = createCustomTempFile(context)
    context.contentResolver.openInputStream(selectedImg)?.use { inputStream ->
        FileOutputStream(myFile).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }

    try {
        ExifInterface(myFile.path).run {
            val rotateAngle = when (getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
            val matrix = Matrix().apply { postRotate(rotateAngle) }
            BitmapFactory.decodeFile(myFile.path)?.let { bitmap ->
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)?.let { result ->
                    FileOutputStream(myFile).use { result.compress(Bitmap.CompressFormat.JPEG, 100, it) }
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return myFile
}

fun reduceImage(filePath: String): Bitmap {
    BitmapFactory.decodeFile(filePath)?.let { bitmap ->
        val matrix = Matrix().apply {
            postRotate(ExifInterface(filePath).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL).toFloat())
        }
        val scaleFactor = if (bitmap.width > 300) {
            300.toFloat() / bitmap.width.toFloat()
        } else {
            1f
        }
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, (bitmap.width * scaleFactor).toInt(), (bitmap.height * scaleFactor).toInt(), false)
        return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
    }
    throw IOException("Failed to decode bitmap from file: $filePath")
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
        val scanning = ArrayList<Recognition>()
        val recognitionsSize = minOf(pq.size, maxResults)
        repeat(recognitionsSize) {
            pq.poll()?.let { scanning.add(it) }
        }
        return scanning
    }
}