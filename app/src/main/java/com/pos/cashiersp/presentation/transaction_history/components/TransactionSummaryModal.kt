package com.pos.cashiersp.presentation.transaction_history.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.pos.cashiersp.model.domain.OrderItem
import com.pos.cashiersp.model.dto.toDomain
import com.pos.cashiersp.presentation.transaction_history.TransactionHistoryViewModel
import com.pos.cashiersp.presentation.ui.theme.Dark
import com.pos.cashiersp.presentation.ui.theme.Gray600
import com.pos.cashiersp.presentation.ui.theme.Light800
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Primary100
import com.pos.cashiersp.presentation.ui.theme.White
import com.pos.cashiersp.presentation.util.toRupiah

/**
 * Data model for a single summary stat row.
 */
private data class SummaryStat(
    val label: String,
    val value: String,
    val description: String
)

/**
A single stat row — leading dot in the primary color, label +
muted description stacked in the middle, and the value pinned
to the end, right-aligned so every number lines up in a column
regardless of digit count. This scans much faster than four
separate cards, since the eye travels down one line instead of
jumping across a grid.
 */
@Composable
private fun SummaryStatRow(
    stat: SummaryStat,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(
            modifier = Modifier
                .size(8.dp)
                .background(color = Primary, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stat.label,
                color = Gray600,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            /*
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stat.description,
                color = Gray500,
                fontSize = 12.sp
            )
             */
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = stat.value,
            color = Dark,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        )
    }
}

/**
The list of stats — a single scrollable-feeling column with
thin dividers between rows instead of separated cards. Wrapped
in a subtle Primary100 surface so it still reads as one grouped
block inside the dialog.
 */
@Composable
private fun SummaryStatsList(
    modifier: Modifier = Modifier,
    viewModel: TransactionHistoryViewModel = hiltViewModel()
) {
    val searchTransactionsDto = viewModel.searchTransactionsDto.value

    if (searchTransactionsDto == null) {
        EmptyDateRangeNotice(modifier = modifier)
        return
    }

    val orderItems: List<OrderItem> = searchTransactionsDto.orderItems.map { it.toDomain() }
    val revenue = orderItems.fold(0) { acc, item -> acc + item.totalAmount }
    val transactionCount = searchTransactionsDto.totalCount

    val stats = listOf(
        SummaryStat(
            label = "Revenue",
            value = revenue.toRupiah(),
            description = "Gross sales"
        ),
        SummaryStat(
            label = "Transactions",
            value = transactionCount.toString(),
            description = "Transaction count from selected range"
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Primary100, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp)
    ) {
        stats.forEachIndexed { index, stat ->
            SummaryStatRow(stat = stat)
            if (index != stats.lastIndex) {
                HorizontalDivider(color = Light800, thickness = 1.dp)
            }
        }
    }
}

/**
Shown in place of the stats list when no date range has been
selected yet, so the user understands what action to take
instead of seeing an empty or broken-looking summary.
 */
@Composable
private fun EmptyDateRangeNotice(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Primary100, shape = RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select a date range to continue",
            color = Dark,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Please choose a transaction date range before viewing the summary.",
            color = Gray600,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

/**
The modal itself, built on top of Compose's Dialog component.
The viewmodel is place at SummaryPrivateList (private component only for this file)
 */
@Composable
fun TransactionSummaryModal(
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = White, shape = RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Text(
                text = "Summary",
                color = Dark,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))

            SummaryStatsList()
        }
    }
}