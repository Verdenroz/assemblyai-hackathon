package com.verdenroz.fiveshades.network

import com.verdenroz.fiveshades.BuildConfig
import com.verdenroz.fiveshades.model.Shade
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import java.io.InputStream


private const val OPEN_AI_URL = "https://api.openai.com/v1/chat/completions"

object OpenAIDataASource : DataSource {

    override suspend fun getByteStream(url: HttpUrl): InputStream {
        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
            .build()
        val call = client.newCall(request)
        val response = call.executeAsync()
        if (!response.isSuccessful) {
            throw Exception("Failed to fetch data from OpenAI API")
        }
        return response.body!!.byteStream()
    }

    override suspend fun getResponseForShade(
        shade: Shade,
        transcript: String
    ): String {
        val systemPrompts = mapOf(
            Shade.RED to "You are ANGRY! ONLY RESPOND IN UPPERCASE AND BE RUDE!" ,
            Shade.YELLOW to "You are a pompous asshole. Be as prideful as possible and believe you are always right.",
            Shade.BLUE to "You are a depressed goth. Be as sad as possible.",
            Shade.GREEN to "You are always jealous. If people are happy, you are not. If people are sad, you are happy.",
            Shade.PURPLE to "You have a god complex. You think you are the last Roman Emperor. Make many references to Rome."
        )

        val systemPrompt = systemPrompts[shade] ?: "You are an assistant."
        val requestBody = """
        {
            "model": "gpt-4o-mini",
            "messages": [
                {"role": "system", "content": "$systemPrompt"},
                {"role": "user", "content": "$transcript"}
            ],
            "temperature": 0.7
        }
    """.trimIndent()

        val request = Request.Builder()
            .url(OPEN_AI_URL)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
            .post(RequestBody.create("application/json".toMediaType(), requestBody))
            .build()

        val call = client.newCall(request)
        val response = call.executeAsync()
        val respnseBody = response.body!!.string()

        val openAIResponse = json.decodeFromString(OpenAIResponse.serializer(), respnseBody)
        return openAIResponse.choices.first().message.content
    }
}