package com.verdenroz.fiveshades.media

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AudioRecorder {
    private val sampleRate = 16000
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
    @SuppressLint("MissingPermission")
    private val audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize)

    suspend fun startRecording(onAudioData: (ByteArray) -> Unit) {
        withContext(Dispatchers.IO) {
            audioRecord.startRecording()
            val buffer = ByteArray(bufferSize)
            while (audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                val read = audioRecord.read(buffer, 0, buffer.size)
                if (read > 0) {
                    onAudioData(buffer.copyOf(read))
                }
            }
        }
    }

    fun stopRecording() {
        audioRecord.stop()
        audioRecord.release()
    }
}