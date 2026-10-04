package com.example.media

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File

class VoiceRecorder(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var currentRecordingFile: File? = null
    private var isRecording = false

    fun startRecording(onStarted: (File) -> Unit, onError: (String) -> Unit) {
        if (isRecording) return
        try {
            val outputDir = File(context.cacheDir, "ws_voiceovers").apply { mkdirs() }
            val outputFile = File(outputDir, "ws_voice_${System.currentTimeMillis()}.m4a")
            currentRecordingFile = outputFile

            val newRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            newRecorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
            }
            recorder = newRecorder
            isRecording = true
            onStarted(outputFile)
        } catch (e: Exception) {
            Log.e("VoiceRecorder", "Failed to start audio recording", e)
            recorder?.release()
            recorder = null
            isRecording = false
            onError(e.localizedMessage ?: "Audio recording failed")
        }
    }

    fun stopRecording(): File? {
        if (!isRecording) return null
        return try {
            recorder?.apply {
                stop()
                release()
            }
            recorder = null
            isRecording = false
            currentRecordingFile
        } catch (e: Exception) {
            Log.e("VoiceRecorder", "Failed to stop audio recording", e)
            recorder?.release()
            recorder = null
            isRecording = false
            null
        }
    }

    fun isCurrentlyRecording(): Boolean = isRecording
}
