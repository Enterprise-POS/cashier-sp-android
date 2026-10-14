package com.pos.cashiersp.presentation.transaction_history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.window.core.layout.WindowSizeClass
import com.pos.cashiersp.presentation.Screen
import com.pos.cashiersp.presentation.global_component.TextWithNoPadding
import com.pos.cashiersp.presentation.transaction_history.components.DateRangePickerSection
import com.pos.cashiersp.presentation.transaction_history.components.SalesReportSection
import com.pos.cashiersp.presentation.transaction_history.components.SearchInvoiceModal
import com.pos.cashiersp.presentation.transaction_history.components.TransactionSummaryModal
import com.pos.cashiersp.presentation.transaction_history.components.TransactionSummarySection
import com.pos.cashiersp.presentation.ui.theme.Gray600
import com.pos.cashiersp.presentation.ui.theme.Light300
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Primary100
import com.pos.cashiersp.presentation.ui.theme.Primary800
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.Secondary100
import com.pos.cashiersp.presentation.ui.theme.White
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(
    navController: NavController,
    viewModel: TransactionHistoryViewModel = hiltViewModel()
) {
    val storeName = viewModel.storeName.value
    val showSearchModal = viewModel.showSearchInvoiceModal.value
    val showSummaryTransactionModal = viewModel.showSummaryTransactionModal.value
    val isExpanded = currentWindowAdaptiveInfo().windowSizeClass.isWidthAtLeastBreakpoint(
        WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND
    )

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is TransactionHistoryViewModel.UIEvent.GotoInvoiceDetailScreen -> navController.navigate(
                    Screen.createInvoiceDetailURL(
                        event.orderItemId
                    )
                )
            }
        }
    }

    Scaffold(
        containerColor = Light300,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
                title = {
                    Column {
                        TextWithNoPadding(
                            "Transaction History",
                            fontSize = 18.sp,
                            color = Secondary,
                            fontWeight = FontWeight.W500,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(6.dp))
                        TextWithNoPadding(
                            "Review and filter past sales",
                            fontSize = 12.sp,
                            color = Gray600,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to previous screen"
                        )
                    }
                },
                actions = {
                    Box(
                        contentAlignment = Alignment.CenterEnd,
                        modifier = Modifier.width(if (isExpanded) 220.dp else 90.dp)
                    ) {
                        Text(
                            storeName,
                            maxLines = if (isExpanded) 2 else 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 11.sp,
                            color = Primary,
                            fontWeight = FontWeight.W500,
                            modifier = Modifier
                                .background(Primary100, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))

                    if (!isExpanded) {
                        IconButton(
                            onClick = {
                                viewModel.onEvent(
                                    TransactionHistoryEvent.OnToggleSummaryTransactionBtn(true)
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Summarize,
                                contentDescription = "View transaction summary",
                                tint = Primary800
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(TransactionHistoryEvent.OnTapFloatingActionBtn) },
                containerColor = Primary,
                contentColor = White,
                shape = CircleShape,
                modifier = Modifier.size(52.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon",
                    tint = White,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                )
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .background(color = Secondary100)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isExpanded) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SalesReportSection(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )

                    Column(
                        modifier = Modifier
                            .widthIn(min = 400.dp, max = 460.dp)
                            .fillMaxHeight()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            DateRangePickerSection()
                            Spacer(Modifier.height(16.dp))
                            TransactionSummarySection(
                                modifier = Modifier.padding(horizontal = 14.dp)
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DateRangePickerSection()
                    SalesReportSection(modifier = Modifier.weight(1f))
                }
            }

            if (showSearchModal) {
                SearchInvoiceModal(
                    onDismiss = { viewModel.onEvent(TransactionHistoryEvent.OnClickCancelAtInvoiceSearchModal) },
                    onConfirm = { viewModel.onEvent(TransactionHistoryEvent.OnClickConfirmAtInvoiceSearchModal) }
                )
            }

            if (showSummaryTransactionModal) {
                TransactionSummaryModal(
                    onDismiss = { viewModel.onEvent(TransactionHistoryEvent.OnToggleSummaryTransactionBtn(false)) }
                )
            }
        }
    }
}