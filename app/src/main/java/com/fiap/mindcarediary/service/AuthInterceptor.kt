package com.fiap.mindcarediary.service

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
class AuthInterceptor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(
        chain: Interceptor.Chain
    ): Response {

        var request = chain.request()

        if (request.url.encodedPath == "/login") {
            return chain.proceed(request)
        }

        val token = runBlocking {
            tokenManager.token.first()
        }

        request = chain.request()
            .newBuilder()
            .apply {

                if (!token.isNullOrBlank()) {
                    addHeader(
                        "Authorization",
                        "Bearer $token"
                    )
                }
            }
            .build()

        return chain.proceed(request)
    }
}