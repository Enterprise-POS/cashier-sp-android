package com.pos.cashiersp.presentation.transaction_history.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pos.cashiersp.presentation.transaction_history.ItemsPerPage
import com.pos.cashiersp.presentation.transaction_history.TransactionHistoryEvent
import com.pos.cashiersp.presentation.transaction_history.TransactionHistoryViewModel
import com.pos.cashiersp.presentation.ui.theme.Dark
import com.pos.cashiersp.presentation.ui.theme.Gray200
import com.pos.cashiersp.presentation.ui.theme.Gray300
import com.pos.cashiersp.presentation.ui.theme.Gray500
import com.pos.cashiersp.presentation.ui.theme.Gray600
import com.pos.cashiersp.presentation.ui.theme.Gray700
import com.pos.cashiersp.presentation.ui.theme.Light600
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.White
import kotlin.math.ceil

@Composable
fun PaginationBar(
    viewModel: TransactionHistoryViewModel,
    modifier: Modifier = Modifier
) {
    val rowsPerPageOptions = ItemsPerPage.entries.map { it }
    var expanded by remember { mutableStateOf(false) }
    val searchTransactionsDto = viewModel.searchTransactionsDto.value
    val itemsPerPage = viewModel.itemsPerPage.value

    val currentPage = searchTransactionsDto?.page ?: 1
    var totalCount = 0
    var page = 1
    var limit = 0
    var totalPages = 0
    if (searchTransactionsDto != null) {
        totalCount = searchTransactionsDto.totalCount
        page = searchTransactionsDto.page
        limit = searchTransactionsDto.limit
        totalPages = ceil(totalCount.toDouble() / limit.toDouble()).toInt()
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rows per page selector
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Rows",
                style = MaterialTheme.typography.bodyMedium,
                color = Secondary,
                fontSize = 13.sp
            )
            Box {
                Surface(
                    modifier = Modifier.clickable { expanded = true },
                    shape = RoundedCornerShape(8.dp),
                    color = Light600,
                    border = BorderStroke(1.dp, Gray200)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = itemsPerPage.value.toString(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Dark,
                            fontSize = 14.sp
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Select rows",
                            tint = Gray600,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    rowsPerPageOptions.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = option.value.toString(),
                                    color = if (option == itemsPerPage) Primary else Dark
                                )
                            },
                            onClick = {
                                viewModel.onEvent(TransactionHistoryEvent.OnClickItemsPerPage(option))
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        // Showing info and pagination
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pagination buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val pages = getPageNumbers(currentPage, totalPages)
                if (pages.isNotEmpty()) {
                    // Previous button
                    PaginationButton(
                        isIcon = true,
                        isActive = false,
                        enabled = currentPage > 1,
                        onClick = { if (currentPage > 1) viewModel.onEvent(TransactionHistoryEvent.OnPageChange(page - 1)) }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Previous",
                            tint = if (currentPage > 1) Gray700 else Gray300,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Page number components
                    pages.forEach { page ->
                        if (page == -1) {
                            Text(
                                text = "...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Gray500,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        } else {
                            PaginationButton(
                                isIcon = false,
                                isActive = page == currentPage,
                                enabled = true,
                                onClick = { viewModel.onEvent(TransactionHistoryEvent.OnPageChange(page)) }
                            ) {
                                Text(
                                    text = page.toString(),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (page == currentPage) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (page == currentPage) White else Gray700,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    // Next button
                    PaginationButton(
                        isIcon = true,
                        isActive = false,
                        enabled = currentPage < totalPages,
                        onClick = {
                            if (currentPage < totalPages)
                                viewModel.onEvent(TransactionHistoryEvent.OnPageChange(page + 1))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Next",
                            tint = if (currentPage > 1) Gray700 else Gray300,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        }
    }

    Spacer(Modifier.height(8.dp))
}

@Composable
fun PaginationButton(
    isIcon: Boolean,
    isActive: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(if (isIcon) 32.dp else 36.dp)
            .clickable(enabled = enabled) { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = when {
            isActive -> Primary
            else -> Color.Transparent
        },
        border = if (!isActive) BorderStroke(1.dp, Gray200) else null
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            content()
        }
    }
}

fun getPageNumbers(currentPage: Int, totalPages: Int, totalButtons: Int = 3): List<Int> {
    if (totalPages <= totalButtons) {
        return (1..totalPages).toList()
    }

    val pages = mutableListOf<Int>()

    // Always show first page
    pages.add(1)

    when {
        currentPage <= 2 -> {
            // Show first 2 pages
            pages.add(2)
            pages.add(-1) // ellipsis
            pages.add(totalPages)
        }

        currentPage >= totalPages - 1 -> {
            // Show last 2 pages
            pages.add(-1) // ellipsis
            pages.add(totalPages - 1)
            pages.add(totalPages)
        }

        else -> {
            // Show current page only
            pages.add(-1) // ellipsis
            pages.add(currentPage)
            pages.add(-1) // ellipsis
            pages.add(totalPages)
        }
    }

    return pages.distinct()
}
