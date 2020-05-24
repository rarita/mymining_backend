package com.raritasolutions.mymining.service.ruz

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class LKAuthService(private val okHttpClient: OkHttpClient,
                    @Value("\${lk.login}") private val lkLogin: String,
                    @Value("\${lk.password}") private val lkPassword: String) {

    private val authFormBody = FormBody.Builder()
            .add("AUTH_FORM", "Y")
            .add("TYPE", "AUTH")
            .add("backurl", "/")
            .add("USER_LOGIN", lkLogin)
            .add("USER_PASSWORD", lkPassword)
            .add("USER_REMEMBER", "Y")
            .build()

    private val rq = Request.Builder()
            .url("https://lk.spmi.ru/ruz/main?login=yes")
            .post(authFormBody)
            .build()

    private val authenticated
        get() = run {
            val rq = Request.Builder()
                    .url("https://lk.spmi.ru/")
                    .get()
                    .build()

            val respString = okHttpClient.newCall(rq)
                    .execute()
                    .body
                    ?.string() ?: throw IOException("LK Service problems, body is null")

            "<title>Авторизация</title>" !in respString
        }

    fun authenticate(): AuthStatus {
        try {
            if (authenticated)
                return AuthStatus.AUTH_ALIVE
        }
        catch (e: IOException) {
            return AuthStatus.LK_SERVICE_ERROR
        }

        val response = okHttpClient.newCall(rq).execute()
        val respString = response.body?.string()
                ?: return AuthStatus.LK_SERVICE_ERROR

        return if ("<title>Горный</title>" in respString)
            AuthStatus.AUTH_SUCCESS
        else
            AuthStatus.AUTH_FAILED

    }

}

enum class AuthStatus {
    AUTH_FAILED,
    AUTH_ALIVE,
    AUTH_SUCCESS,
    LK_SERVICE_ERROR
}