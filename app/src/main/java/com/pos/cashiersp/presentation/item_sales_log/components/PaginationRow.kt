package com.pos.cashiersp.presentation.item_sales_log.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pos.cashiersp.presentation.ui.theme.Gray100
import com.pos.cashiersp.presentation.ui.theme.Gray400
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.White

@Composable
fun PaginationRow(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit
) {
    if (totalPages <= 0) return // nothing to paginate

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PageArrowButton(
            label = "<",
            enabled = currentPage > 1,
            onClick = { onPageChange(currentPage - 1) }
        )
        Spacer(modifier = Modifier.width(6.dp))

        for (page in visiblePages(currentPage, totalPages)) {
            if (page == ELLIPSIS) {
                Text(
                    text = "...",
                    modifier = Modifier.padding(horizontal = 4.dp),
                    color = Gray400
                )
            } else {
                PageNumberButton(
                    number = page,
                    isActive = currentPage == page,
                    onClick = { onPageChange(page) }
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
        }

        PageArrowButton(
            label = ">",
            enabled = currentPage < totalPages,
            onClick = { onPageChange(currentPage + 1) }
        )
    }
}

private const val ELLIPSIS = -1

/**
 * Builds the list of page numbers/ellipsis markers to show, always keeping
 * first page, last page, and a window around currentPage — e.g. for
 * currentPage=5, totalPages=10 -> [1, ..., 4, 5, 6, ..., 10]
 */
private fun visiblePages(currentPage: Int, totalPages: Int): List<Int> {
    if (totalPages <= 5) {
        return (1..totalPages).toList()
    }

    val pages = mutableListOf(1)

    val windowStart = (currentPage - 1).coerceAtLeast(2)
    val windowEnd = (currentPage + 1).coerceAtMost(totalPages - 1)

    if (windowStart > 2) pages.add(ELLIPSIS)
    pages.addAll(windowStart..windowEnd)
    if (windowEnd < totalPages - 1) pages.add(ELLIPSIS)

    pages.add(totalPages)
    return pages
}

@Composable
private fun PageNumberButton(number: Int, isActive: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isActive) Primary else White)
            .border(1.dp, if (isActive) Primary else Gray100, RoundedCornerShape(8.dp))
            .clickable(enabled = !isActive, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$number",
            style = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (isActive) White else Secondary
            )
        )
    }
}

@Composable
private fun PageArrowButton(label: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(White)
            .border(1.dp, Gray100, RoundedCornerShape(8.dp))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 13.sp,
                color = if (enabled) Secondary else Gray400
            )
        )
    }
}