package com.rezio23.translatex.network

import com.google.gson.JsonParser
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object TranslatorClient {

    // TODO: Replace with your RapidAPI key from https://rapidapi.com/joshimuddin8212/api/free-google-translator
    private const val RAPIDAPI_KEY = "YOUR_RAPIDAPI_KEY"
    private const val RAPIDAPI_HOST = "free-google-translator.p.rapidapi.com"
    private const val BASE_URL = "https://free-google-translator.p.rapidapi.com/external-api/free-google-translator"

    private val authInterceptor = AuthInterceptor(RAPIDAPI_KEY, RAPIDAPI_HOST)

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun translate(text: String, fromLang: String, toLang: String): String {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            val jsonBody = """{"translate":"rapidapi"}"""
            val body = jsonBody.toRequestBody("application/json".toMediaType())

            val from = if (fromLang == "auto") "auto" else fromLang

            val request = Request.Builder()
                .url("$BASE_URL?from=$from&to=$toLang&string=${java.net.URLEncoder.encode(text, "UTF-8")}")
                .post(body)
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: throw Exception("Empty response")

            if (!response.isSuccessful) {
                when (response.code) {
                    401, 403 -> throw Exception("Invalid RapidAPI key. Please update it in TranslatorClient.kt")
                    429 -> throw Exception("Rate limit exceeded. Please try again later.")
                    else -> throw Exception("API error: ${response.code}")
                }
            }

            // Parse: response is plain translated text or JSON
            try {
                val json = JsonParser.parseString(responseBody).asJsonObject
                json.get("translation")?.asString
                    ?: json.get("translatedText")?.asString
                    ?: responseBody
            } catch (e: Exception) {
                responseBody.trim()
            }
        }
    }
}
