package com.pemmob.schedulyy.data.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

data class ProcessedImage(
    val uri: Uri,
    val base64Data: String,
    val mimeType: String = "image/jpeg",
    val previewBitmap: Bitmap? = null
)

@Singleton
class ImagePreprocessor @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context
) {
    suspend fun processImageUri(uri: Uri): ProcessedImage? = withContext(Dispatchers.IO) {
        try {
            // 1. Baca ukuran asli tanpa alokasi memori
            val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, boundsOptions)
            }

            val originalWidth = boundsOptions.outWidth
            val originalHeight = boundsOptions.outHeight
            if (originalWidth <= 0 || originalHeight <= 0) return@withContext null

            // 2. Tentukan downsample factor
            val maxDimension = max(originalWidth, originalHeight)
            var sampleSize = 1
            while ((maxDimension / sampleSize) > MAX_EDGE_PX * 1.5) {
                sampleSize *= 2
            }

            // 3. Decode bitmap terkompresi
            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.RGB_565 // Menghemat memori RAM
            }

            val rawBitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, decodeOptions)
            } ?: return@withContext null

            // 4. Koreksi rotasi EXIF
            val rotationDegrees = getExifRotation(context, uri)
            val orientedBitmap = if (rotationDegrees != 0f) {
                val matrix = Matrix().apply { postRotate(rotationDegrees) }
                Bitmap.createBitmap(rawBitmap, 0, 0, rawBitmap.width, rawBitmap.height, matrix, true).also {
                    if (it != rawBitmap) rawBitmap.recycle()
                }
            } else {
                rawBitmap
            }

            // 5. Scale down jika sisi terpanjang masih > MAX_EDGE_PX
            val currentMax = max(orientedBitmap.width, orientedBitmap.height)
            val finalBitmap = if (currentMax > MAX_EDGE_PX) {
                val scale = MAX_EDGE_PX.toFloat() / currentMax
                val targetW = (orientedBitmap.width * scale).toInt()
                val targetH = (orientedBitmap.height * scale).toInt()
                Bitmap.createScaledBitmap(orientedBitmap, targetW, targetH, true).also {
                    if (it != orientedBitmap) orientedBitmap.recycle()
                }
            } else {
                orientedBitmap
            }

            // 6. Kompresi JPEG ~85%
            val outputStream = ByteArrayOutputStream()
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            val imageBytes = outputStream.toByteArray()

            // 7. Base64 encoding
            val base64 = Base64.getEncoder().encodeToString(imageBytes)

            ProcessedImage(
                uri = uri,
                base64Data = base64,
                mimeType = "image/jpeg",
                previewBitmap = finalBitmap
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getExifRotation(context: Context, uri: Uri): Float {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            if (inputStream != null) {
                val exif = ExifInterface(inputStream)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                    else -> 0f
                }
            } else 0f
        } catch (e: Exception) {
            0f
        }
    }

    companion object {
        private const val MAX_EDGE_PX = 1600
    }
}
