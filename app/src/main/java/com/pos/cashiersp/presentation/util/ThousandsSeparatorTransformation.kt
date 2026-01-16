package com.pos.cashiersp.presentation.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class ThousandsSeparatorTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        val numbersOnly = originalText.filter { it.isDigit() }

        val formatted = if (numbersOnly.isNotEmpty()) {
            numbersOnly.reversed()
                .chunked(3)
                .joinToString(",")
                .reversed()
        } else {
            ""
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset == 0) return 0
                val commas = (offset - 1) / 3
                return offset + commas
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset == 0) return 0
                val commas = formatted.take(offset).count { it == ',' }
                return offset - commas
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}