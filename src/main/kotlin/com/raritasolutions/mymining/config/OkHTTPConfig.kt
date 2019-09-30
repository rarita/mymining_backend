package com.raritasolutions.mymining.config

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OkHTTPConfig {

    @Bean
    fun okHttpClient(cookieJar: CookieJar, interceptor: HttpLoggingInterceptor) = OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .addInterceptor(interceptor)
            .build()

    @Bean
    fun cookieJar() = object : CookieJar {
        private val cookieVault = mutableMapOf<String, List<Cookie>>()

        override fun loadForRequest(url: HttpUrl): List<Cookie>
                = cookieVault[url.host]
                        ?.filter { it.path in url.encodedPath }
                        ?: listOf()

        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            val host = url.host
            if (cookieVault[host] != null) // Consider .putIfAbsent()
                cookieVault[host] = cookieVault[host]!!.plus(cookies)
            else
                cookieVault[host] = cookies
        }
    }

    @Bean
    fun interceptor() = HttpLoggingInterceptor()
            .apply { level = HttpLoggingInterceptor.Level.HEADERS }

}