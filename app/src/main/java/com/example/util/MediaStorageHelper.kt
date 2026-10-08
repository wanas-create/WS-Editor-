package com.example.util

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Utility for persisting picked content URIs into internal storage so projects
 * remain fully readable across app restarts and process recreation.
 */
object MediaStorageHelper {

    private const val TAG = "MediaStorageHelper"

    /**
     * Copies a media URI into the app's internal files directory if needed.
     * If the URI is already a local app file, returns it as-is.
     * Returns an absolute local file path or file:// URI string.
     */
    fun persistMediaLocally(context: Context, uri: Uri, prefix: String = "media"): String {
        val uriString = uri.toString()

        // If it's already an existing internal file, keep it
        if (uriString.startsWith("file://")) {
            val path = uri.path
            if (path != null && File(path).exists()) {
                return uriString
            }
        }
        val directFile = File(uriString)
        if (directFile.exists()) {
            return directFile.absolutePath
        }

        // Try persistable URI permission grant if possible
        try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (_: Throwable) {
            // Not all content providers support takePersistableUriPermission; we copy as a robust guarantee
        }

        return try {
            val projectMediaDir = File(context.filesDir, "project_media").apply { mkdirs() }
            val extension = getExtension(context, uri)
            val targetFile = File(projectMediaDir, "${prefix}_${System.currentTimeMillis()}_${(1000..9999).random()}.$extension")

            var inputStream: InputStream? = null
            var outputStream: FileOutputStream? = null
            try {
                inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    outputStream = FileOutputStream(targetFile)
                    val buffer = ByteArray(32 * 1024)
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                    outputStream.flush()
                    Log.d(TAG, "Copied media to persistent internal file: ${targetFile.absolutePath}")
                    targetFile.absolutePath
                } else {
                    uriString
                }
            } finally {
                try { inputStream?.close() } catch (_: Throwable) {}
                try { outputStream?.close() } catch (_: Throwable) {}
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Failed copying media file locally: ${e.message}", e)
            uriString
        }
    }

    private fun getExtension(context: Context, uri: Uri): String {
        try {
            val mime = context.contentResolver.getType(uri)
            if (mime != null) {
                if (mime.contains("video/mp4")) return "mp4"
                if (mime.contains("video/")) return "mp4"
                if (mime.contains("image/png")) return "png"
                if (mime.contains("image/jpeg") || mime.contains("image/jpg")) return "jpg"
                if (mime.contains("audio/")) return "mp3"
            }
            val lastSegment = uri.lastPathSegment
            if (lastSegment != null && lastSegment.contains(".")) {
                return lastSegment.substringAfterLast(".")
            }
        } catch (_: Throwable) {}
        return "bin"
    }
}
