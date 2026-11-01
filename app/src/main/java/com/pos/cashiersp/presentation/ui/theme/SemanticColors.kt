package com.pos.cashiersp.presentation.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class SemanticColorTokens(
    val foreground: Color,
    val container: Color,
    val onContainer: Color,
)

data class PosSemanticColors(
    val success: SemanticColorTokens,
    val warning: SemanticColorTokens,
    val information: SemanticColorTokens,
    val error: SemanticColorTokens,
    val paid: SemanticColorTokens,
    val pending: SemanticColorTokens,
    val failed: SemanticColorTokens,
    val refunded: SemanticColorTokens,
    val cancelled: SemanticColorTokens,
)

internal val LightPosSemanticColors = PosSemanticColors(
    success = SemanticColorTokens(Color(0xFF1B6E3B), Color(0xFFD7F2DE), Color(0xFF123B22)),
    warning = SemanticColorTokens(Color(0xFF765000), Color(0xFFFFF2CC), Color(0xFF3B2E00)),
    information = SemanticColorTokens(Color(0xFF005AA7), Color(0xFFD6E8FF), Color(0xFF102F4A)),
    error = SemanticColorTokens(Color(0xFFBA1A1A), Color(0xFFFFDAD6), Color(0xFF410002)),
    paid = SemanticColorTokens(Color(0xFF1B6E3B), Color(0xFFD7F2DE), Color(0xFF123B22)),
    pending = SemanticColorTokens(Color(0xFF765000), Color(0xFFFFF2CC), Color(0xFF3B2E00)),
    failed = SemanticColorTokens(Color(0xFFBA1A1A), Color(0xFFFFDAD6), Color(0xFF410002)),
    refunded = SemanticColorTokens(Color(0xFF005AA7), Color(0xFFD6E8FF), Color(0xFF102F4A)),
    cancelled = SemanticColorTokens(Color(0xFF41474D), Color(0xFFE7E9ED), Color(0xFF41474D)),
)

internal val DarkPosSemanticColors = PosSemanticColors(
    success = SemanticColorTokens(Color(0xFF8CD99D), Color(0xFF173622), Color(0xFF8CD99D)),
    warning = SemanticColorTokens(Color(0xFFFFD166), Color(0xFF493400), Color(0xFFFFD166)),
    information = SemanticColorTokens(Color(0xFF9CCBFF), Color(0xFF102F4A), Color(0xFF9CCBFF)),
    error = SemanticColorTokens(Color(0xFFFFB4AB), Color(0xFF93000A), Color(0xFFFFDAD6)),
    paid = SemanticColorTokens(Color(0xFF8CD99D), Color(0xFF173622), Color(0xFF8CD99D)),
    pending = SemanticColorTokens(Color(0xFFFFD166), Color(0xFF493400), Color(0xFFFFD166)),
    failed = SemanticColorTokens(Color(0xFFFFB4AB), Color(0xFF93000A), Color(0xFFFFDAD6)),
    refunded = SemanticColorTokens(Color(0xFF9CCBFF), Color(0xFF102F4A), Color(0xFF9CCBFF)),
    cancelled = SemanticColorTokens(Color(0xFFC1C7CE), Color(0xFF42474D), Color(0xFFC1C7CE)),
)

val LocalPosSemanticColors = staticCompositionLocalOf { LightPosSemanticColors }
