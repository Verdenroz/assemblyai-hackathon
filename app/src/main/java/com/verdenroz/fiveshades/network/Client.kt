package com.verdenroz.fiveshades.network

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resumeWithException

val client = OkHttpClient.Builder()
    .build()

val json = Json {
    ignoreUnknownKeys = true
}

/**
 * Extension function to execute a [Call] asynchronously
 */
@OptIn(ExperimentalCoroutinesApi::class)
suspend fun Call.executeAsync(): Response = suspendCancellableCoroutine { continuation ->
    continuation.invokeOnCancellation {
        this.cancel()
    }
    this.enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            continuation.resumeWithException(e)
        }

        override fun onResponse(call: Call, response: Response) {
            continuation.resume(value = response, onCancellation = { call.cancel() })
        }
    })
}