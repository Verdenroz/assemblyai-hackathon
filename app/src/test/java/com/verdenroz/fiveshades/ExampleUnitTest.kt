package com.verdenroz.fiveshades

import com.verdenroz.fiveshades.model.Shade
import com.verdenroz.fiveshades.network.OpenAIDataASource
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun testOpenAI() {
        val openAI = OpenAIDataASource
        runBlocking {
            val response = openAI.getResponseForShade(Shade.RED, "This is a test")
            println(response)
        }
    }
}