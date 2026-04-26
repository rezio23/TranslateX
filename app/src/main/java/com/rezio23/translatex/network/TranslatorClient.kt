package com.rezio23.translatex.network

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object TranslatorClient {

    private const val RAPIDAPI_KEY = "ab5bfacfe9msh72dc7284a022c12p12402ajsnbcb3662748e9"
    private const val RAPIDAPI_HOST = "google-api31.p.rapidapi.com"
    private const val BASE_URL = "https://google-api31.p.rapidapi.com/translate"

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
            
            val jsonObject = JsonObject().apply {
                addProperty("text", text)
                addProperty("to", toLang)
                // Reverting to empty string for auto-detect based on API docs in screenshot
                addProperty("from_lang", if (fromLang == "auto") "" else fromLang)
            }
            
            val body = jsonObject.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(BASE_URL)
                .post(body)
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                when (response.code) {
                    401, 403 -> throw Exception("Subscription Required. Please click 'Subscribe to Test' on the RapidAPI page.")
                    429 -> throw Exception("Rate limit reached. Wait a few seconds.")
                    else -> throw Exception("API Error ${response.code}: $responseBody")
                }
            }

            try {
                val jsonElement = JsonParser.parseString(responseBody)
                
                if (jsonElement.isJsonObject) {
                    val obj = jsonElement.asJsonObject
                    if (obj.has("Error")) {
                        return@withContext "Error: " + obj.get("Error").asString
                    }
                }

                if (jsonElement.isJsonArray) {
                    val firstObject = jsonElement.asJsonArray[0].asJsonObject
                    firstObject.get("translated")?.asString ?: responseBody
                } else if (jsonElement.isJsonObject) {
                    val obj = jsonElement.asJsonObject
                    obj.get("translated")?.asString
                        ?: obj.get("translated_text")?.asString
                        ?: responseBody
                } else {
                    responseBody.trim()
                }
            } catch (e: Exception) {
                responseBody.trim().takeIf { it.isNotEmpty() } ?: "Error: Empty response"
            }
        }
    }
}
