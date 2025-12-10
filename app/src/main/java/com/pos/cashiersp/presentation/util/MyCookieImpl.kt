package com.pos.cashiersp.presentation.util

import com.pos.cashiersp.BuildConfig
import com.pos.cashiersp.common.Constants
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl

class MyCookieImpl : CookieJar {

    private var cookies: MutableList<Cookie> = mutableListOf()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        synchronized(this.cookies) {
            // Remove expired cookies and duplicates, then add new ones
            cookies.forEach { newCookie ->
                this.cookies.removeAll { existing ->
                    existing.name == newCookie.name &&
                            existing.domain == newCookie.domain &&
                            existing.path == newCookie.path
                }

                // Only add if not expired
                if (!isExpired(newCookie)) {
                    this.cookies.add(newCookie)
                }
            }
        }
    }


    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        synchronized(cookies) {
            // Remove expired cookies
            cookies.removeAll { isExpired(it) }

            // Return only cookies that match this URL
            return cookies.filter { cookie ->
                matchesDomain(url, cookie) && matchesPath(url, cookie)
            }
        }
    }

    private fun isExpired(cookie: Cookie): Boolean {
        return cookie.expiresAt < System.currentTimeMillis()
    }

    private fun matchesDomain(url: HttpUrl, cookie: Cookie): Boolean {
        val urlHost = url.host
        val cookieDomain = cookie.domain

        return when {
            // Rule 1: Exact domain match
            urlHost == cookieDomain -> true

            // Rule 2: Wildcard domain match (cookie domain starts with ".")
            cookieDomain.startsWith(".") -> {
                urlHost.endsWith(cookieDomain.substring(1)) ||
                        urlHost == cookieDomain.substring(1)
            }

            // Rule 3: No match for any other case
            else -> false
        }
    }

    private fun matchesPath(url: HttpUrl, cookie: Cookie): Boolean {
        val urlPath = url.encodedPath
        val cookiePath = cookie.path

        return urlPath == cookiePath ||
                (urlPath.startsWith(cookiePath) &&
                        (cookiePath.endsWith("/") || urlPath[cookiePath.length] == '/'))
    }

    // If you must set manually cookie
    fun setCookie(cookie: Cookie) {
        synchronized(cookies) {
            cookies.removeAll {
                it.name == cookie.name &&
                        it.domain == cookie.domain &&
                        it.path == cookie.path
            }
            cookies.add(cookie)
        }
    }

    fun restoreCookie(token: String) {
        val host = Constants.BASE_BE_URL.toHttpUrl().host

        val cookieBuilder = Cookie.Builder()
            .domain(host)
            .path("/")
            .name(Constants.BE_COOKIE_NAME)
            .value(token)
            .expiresAt(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000) // 30 days

        val cookie = if (BuildConfig.MODE == "prod") {
            cookieBuilder.secure().httpOnly().build()
        } else {
            cookieBuilder.httpOnly().build()
        }

        setCookie(cookie)
    }
}
