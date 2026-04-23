package com.rezio23.translatex.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp Interceptor that automatically adds RapidAPI authentication headers
 * to every outgoing request. This is the standard way to inject auth headers
 * using OkHttp's interceptor pipeline.
 */
class AuthInterceptor(
    private val apiKey: String,
    private val apiHost: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val authenticatedRequest = originalRequest.newBuilder()
            .header("x-rapidapi-key", apiKey)
            .header("x-rapidapi-host", apiHost)
            .header("Content-Type", "application/json")
            .build()
        return chain.proceed(authenticatedRequest)
    }
}
