package com.verdenroz.fiveshades.network

import com.verdenroz.fiveshades.model.Shade
import okhttp3.HttpUrl
import java.io.InputStream

interface DataSource {

    suspend fun getByteStream(url: HttpUrl): InputStream

    suspend fun getResponseForShade(shade: Shade, transcript: String): String

    companion object
}