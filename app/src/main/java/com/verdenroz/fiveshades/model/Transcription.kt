package com.verdenroz.fiveshades.model

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Stable
@Serializable
data class Transcription(
    val text: String,
    val isFinal: Boolean = false,
)
