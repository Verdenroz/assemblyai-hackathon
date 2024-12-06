package com.verdenroz.fiveshades.media

import com.assemblyai.api.RealtimeTranscriber
import com.verdenroz.fiveshades.model.Transcription
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow

interface Transcriber {
    var audioRecorder: AudioRecorder?
    val realtimeTranscriber: RealtimeTranscriber
    val isListening: Flow<Boolean>

    fun start()

    fun stop()

    fun getUserChannel(): Channel<Transcription>

    companion object
}