package com.verdenroz.fiveshades.media

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.assemblyai.api.RealtimeTranscriber
import com.verdenroz.fiveshades.BuildConfig
import com.verdenroz.fiveshades.model.Transcription
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ImplTranscriber private constructor(private val context: Context): Transcriber {
    private val userChannel = Channel<Transcription>()

    override var audioRecorder: AudioRecorder? = null
    override val realtimeTranscriber = RealtimeTranscriber.builder()
        .apiKey(BuildConfig.ASSEMBLY_APP_SECRET)
        .sampleRate(16000)
        .onSessionBegins { session ->
            println("Session opened with ID: " + session.sessionId)
        }
        .onPartialTranscript { transcript ->
            CoroutineScope(Dispatchers.IO).launch {
                println("Partial transcript: " + transcript.text)
                userChannel.send(Transcription(transcript.text))
            }
        }
        .onFinalTranscript { transcript ->
            CoroutineScope(Dispatchers.IO).launch {
                println("Final transcript: " + transcript.text)
                userChannel.send(Transcription(transcript.text, true))

                // Stop recording when final transcript is received
                stop()
            }
        }
        .onError { error ->
            println("Error: " + error.message)
        }
        .build()

    private val _isListening = MutableStateFlow(false)
    override val isListening: Flow<Boolean> = _isListening

    override fun start() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // Handle permission not granted
            return
        }

        audioRecorder = AudioRecorder()
        audioRecorder?.let {
            realtimeTranscriber.connect()
            _isListening.value = true
            CoroutineScope(Dispatchers.IO).launch {
                it.startRecording { audioData ->
                    realtimeTranscriber.sendAudio(audioData)
                }
            }
        }
    }

    override fun stop() {
        audioRecorder?.stopRecording()
        realtimeTranscriber.close()
        _isListening.value = false
    }

    override fun getUserChannel() = userChannel

    companion object {
        private var instance: Transcriber? = null

        /**
         * Get the implementation of [Transcriber]
         */
        @Synchronized
        fun Transcriber.Companion.get(
            application: Application
        ): Transcriber {
            if (instance == null) {
                instance = ImplTranscriber(application)
            }

            return instance!!
        }
    }
}