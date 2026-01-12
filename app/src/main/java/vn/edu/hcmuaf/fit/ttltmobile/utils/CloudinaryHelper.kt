package vn.edu.hcmuaf.fit.ttltmobile.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import java.io.File
import java.io.FileOutputStream

object CloudinaryHelper {
    private const val TAG = "CloudinaryHelper"
    private var isInitialized = false

    private const val CLOUD_NAME = "dbpvcjmk0"
    private const val UPLOAD_PRESET = "android_coffee_upload"
    private const val API_KEY = ""
    private const val API_SECRET = ""

    fun initialize(context: Context) {
        if (!isInitialized) {
            try {
                val config = HashMap<String, String>()
                config["cloud_name"] = CLOUD_NAME

                MediaManager.init(context, config)
                isInitialized = true
                Log.d(TAG, "Cloudinary initialized successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize Cloudinary", e)
            }
        }
    }

    fun uploadImage(
        context: Context,
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        if (!isInitialized) {
            initialize(context)
        }

        try {
            // Convert Uri to File
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            // Upload to Cloudinary
            MediaManager.get().upload(file.absolutePath)
                .unsigned(UPLOAD_PRESET)
                .option("resource_type", "image")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {
                        Log.d(TAG, "Upload started: $requestId")
                    }

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                        val progress = (bytes.toDouble() / totalBytes.toDouble() * 100).toInt()
                        Log.d(TAG, "Upload progress: $progress%")
                    }

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val url = resultData["secure_url"] as? String
                        if (url != null) {
                            Log.d(TAG, "Upload successful: $url")
                            onSuccess(url)
                        } else {
                            onError("Không lấy được URL ảnh")
                        }

                        // Clean up temp file
                        file.delete()
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        Log.e(TAG, "Upload error: ${error.description}")
                        onError(error.description ?: "Lỗi khi upload ảnh")

                        // Clean up temp file
                        file.delete()
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        Log.w(TAG, "Upload rescheduled: ${error.description}")
                    }
                })
                .dispatch()

        } catch (e: Exception) {
            Log.e(TAG, "Exception during upload", e)
            onError("Lỗi: ${e.message}")
        }
    }
}