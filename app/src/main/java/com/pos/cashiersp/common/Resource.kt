package com.pos.cashiersp.common

import com.pos.cashiersp.BuildConfig
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}

typealias ResponseStatus = String

const val success: ResponseStatus = "success"
const val error: ResponseStatus = "error"

@Deprecated("If MyCookieImpl don't have problem then this class will be deleted")
class InMemoryCookieJar : CookieJar {
    private val cookieStore = HashMap<HttpUrl, List<Cookie>>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore[url] = cookies
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore[url] ?: emptyList()
    }

    fun restoreCookie(token: String) {
        val host = Constants.BASE_BE_URL
            .replace("http://", "")
            .replace("https://", "")
            .trimEnd('/')

        val cookieBuilder = Cookie.Builder()
            .domain(host)
            .path("/")
            .name(Constants.BE_COOKIE_NAME)
            .value(token)

        val cookie: Cookie
        if (BuildConfig.MODE == "prod") {
            cookie = cookieBuilder.secure() // only use if backend uses https
                .httpOnly()
                .build()
        } else {
            cookie = cookieBuilder
                .httpOnly()
                .build()
        }

        saveFromResponse(Constants.BASE_BE_URL.toHttpUrl(), listOf(cookie))
    }
}

sealed class HTTPStatus<out T> {
    data class SuccessResponse<T>(
        val code: Int,
        val status: ResponseStatus,
        val data: T,
    ) : HTTPStatus<T>()

    data class ErrorResponse(
        val code: Int,
        val status: ResponseStatus,
        val message: String = "", // Error message
    ) : HTTPStatus<Nothing>()
}
