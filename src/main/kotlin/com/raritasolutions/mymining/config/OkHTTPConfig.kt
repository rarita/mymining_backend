package com.raritasolutions.mymining.config

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

@Configuration
class OkHTTPConfig {

    @Value("\${network.ignore-ssl}")
    private val ignoreSSL: Boolean = true

    @Bean
    fun okHttpClient(cookieJar: CookieJar,
                     interceptor: HttpLoggingInterceptor,
                     socketFactory: SSLSocketFactory,
                     hostnameVerifier: HostnameVerifier) : OkHttpClient {

        val clientBuilder
                = OkHttpClient.Builder()
                    .cookieJar(cookieJar)
                    .addInterceptor(interceptor)

        if (ignoreSSL) {
            clientBuilder.sslSocketFactory(socketFactory, unsafeTrustManager()[0] as X509TrustManager)
            clientBuilder.hostnameVerifier(hostnameVerifier)
        }

        return clientBuilder.build()

    }


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
    fun unsafeTrustManager(): Array<TrustManager> = arrayOf(
            object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {}
                override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) {}
                override fun getAcceptedIssuers(): Array<X509Certificate?>? { return arrayOfNulls(0) }
            }
    )

    @Bean
    fun unsafeSSLSocketFactory(trustManager: Array<TrustManager>): SSLSocketFactory
            = SSLContext.getInstance("SSL")
                .apply { init(null, trustManager, SecureRandom()) }
                .socketFactory

    @Bean
    fun unsafeHostNameVerifier()
        = HostnameVerifier { _, _ -> return@HostnameVerifier true }

    @Bean
    fun interceptor() = HttpLoggingInterceptor()
            .apply { level = HttpLoggingInterceptor.Level.NONE }

}