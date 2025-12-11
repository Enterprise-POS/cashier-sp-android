package com.pos.cashiersp.presentation.util

fun getInitials(name: String): String {
    // Split by spaces, remove empty parts
    val parts = name.trim()
        .split("\\s+".toRegex())
        .filter { it.isNotEmpty() }

    if (parts.isEmpty()) return ""

    // Always take first name's first letter
    val first = parts.first()[0]

    // Take second part only if it exists AND is not a single-letter trailing part
    val second = when {
        parts.size >= 2 && parts[1].length > 1 -> parts[1][0]
        parts.size >= 3 -> parts[1][0] // if second is short, use third
        else -> null
    }

    return buildString {
        append(first)
        second?.let { append(it) }
    }.uppercase()
}