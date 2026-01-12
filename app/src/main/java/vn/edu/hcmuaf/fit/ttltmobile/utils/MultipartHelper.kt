package vn.edu.hcmuaf.fit.ttltmobile.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

object MultipartHelper {

    fun createPartFromString(value: String?): RequestBody? {
        return value?.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    fun createPartFromBoolean(value: Boolean?): RequestBody? {
        return value?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    fun createImagePart(context: Context, imageUri: Uri, partName: String = "avatar"): MultipartBody.Part? {
        try {
            // Convert Uri to File
            val file = uriToFile(context, imageUri) ?: return null

            // Determine MIME type
            val mimeType = context.contentResolver.getType(imageUri) ?: "image/jpeg"

            // Create RequestBody
            val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())

            // Create MultipartBody.Part
            return MultipartBody.Part.createFormData(partName, file.name, requestBody)

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null

            // Get original filename
            val fileName = getFileName(context, uri)

            // Create temp file in cache directory
            val file = File(context.cacheDir, fileName)
            val outputStream = FileOutputStream(file)

            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()

            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getFileName(context: Context, uri: Uri): String {
        var fileName = "temp_image_${System.currentTimeMillis()}.jpg"

        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    fileName = cursor.getString(nameIndex)
                }
            }
        }

        return fileName
    }

    fun isValidImageFile(context: Context, uri: Uri): Boolean {
        val mimeType = context.contentResolver.getType(uri) ?: return false
        return mimeType.startsWith("image/")
    }

    fun getFileSizeInMB(context: Context, uri: Uri): Double {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return 0.0
        val sizeInBytes = inputStream.available()
        inputStream.close()
        return sizeInBytes / (1024.0 * 1024.0)
    }
}