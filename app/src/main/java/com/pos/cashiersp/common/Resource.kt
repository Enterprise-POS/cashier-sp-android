package com.pos.cashiersp.common

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}

typealias ResponseStatus = String
const val success: ResponseStatus = "success"
const val error: ResponseStatus = "error"

class InMemoryCookieJar : CookieJar {
    private val cookieStore = HashMap<HttpUrl, List<Cookie>>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore[url] = cookies
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore[url] ?: emptyList()
    }
}

sealed class HTTPStatus<out T> {
    data class SuccessResponse<T>(
        val code: Int,
        val status: ResponseStatus,
        val data: T,
    ): HTTPStatus<T>()

    data class ErrorResponse(
        val code: Int,
        val status: ResponseStatus,
        val message: String = "", // Error message
    ): HTTPStatus<Nothing>()
}
