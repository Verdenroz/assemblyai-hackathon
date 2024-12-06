package com.verdenroz.fiveshades.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.verdenroz.fiveshades.media.ImplTranscriber.Companion.get
import com.verdenroz.fiveshades.media.Transcriber
import com.verdenroz.fiveshades.model.Response
import com.verdenroz.fiveshades.model.Shade
import com.verdenroz.fiveshades.model.Transcription
import com.verdenroz.fiveshades.network.OpenAIDataASource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val transcriber = Transcriber.get(application)
    private val openAI = OpenAIDataASource

    val shade = MutableStateFlow(Shade.entries.toTypedArray().random())

    private val _transcriptions = MutableStateFlow<List<Transcription>>(emptyList())
    val transcriptions: StateFlow<List<Transcription>> = _transcriptions.asStateFlow()

    private val _responses = MutableStateFlow<List<Response>>(emptyList())
    val responses: StateFlow<List<Response>> = _responses.asStateFlow()

    val isListening: StateFlow<Boolean> = transcriber.isListening.stateIn(
        viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = false
    )

    init {
        viewModelScope.launch {
            for (transcription in transcriber.getUserChannel()) {
                if (transcription.text.isNotBlank()) {
                    val currentTranscriptions = _transcriptions.value.toMutableList()
                    if (currentTranscriptions.isNotEmpty() && !currentTranscriptions.last().isFinal) {
                        currentTranscriptions[currentTranscriptions.size - 1] = transcription
                    } else {
                        currentTranscriptions.add(transcription)
                    }
                    _transcriptions.value = currentTranscriptions
                    Log.d("HomeViewModel", "Transcription: ${transcription.text} ${transcriptions.value}")

                    if (transcription.isFinal) {
                        _responses.value += Response(isLoading = true)
                        val responseMessage = openAI.getResponseForShade(shade.value, transcription.text)
                        _responses.value = _responses.value.dropLast(1) + Response(message = responseMessage, isLoading = false)
                    }
                }
            }
        }
    }

    fun startTranscription() {
        viewModelScope.launch {
            transcriber.start()
        }
    }

    fun stopTranscription() {
        viewModelScope.launch {
            transcriber.stop()
        }
    }

    fun onPreviousShade() {
        shade.value = Shade.previous(shade.value)
        _responses.value = emptyList()
        _transcriptions.value = emptyList()
    }

    fun onNextShade() {
        shade.value = Shade.next(shade.value)
        _responses.value = emptyList()
        _transcriptions.value = emptyList()
    }
}