package com.pos.cashiersp.presentation.invoice_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.pos.cashiersp.presentation.Screen
import com.pos.cashiersp.presentation.cashier.component.GeneralAlertDialog
import com.pos.cashiersp.presentation.cashier.component.GeneralAlertDialogStatus
import com.pos.cashiersp.presentation.global_component.TextWithNoPadding
import com.pos.cashiersp.presentation.invoice_detail.component.InvoiceHeaderCard
import com.pos.cashiersp.presentation.invoice_detail.component.PaymentSummarySection
import com.pos.cashiersp.presentation.invoice_detail.component.PrintReceiptButton
import com.pos.cashiersp.presentation.invoice_detail.component.PurchasedItemsSection
import com.pos.cashiersp.presentation.ui.theme.Danger
import com.pos.cashiersp.presentation.ui.theme.Danger400
import com.pos.cashiersp.presentation.ui.theme.Danger800
import com.pos.cashiersp.presentation.ui.theme.Gray600
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.Secondary100
import com.pos.cashiersp.presentation.ui.theme.White
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceDetailScreen(
    navController: NavController,
    viewModel: InvoiceDetailViewModel = hiltViewModel()
) {
    val generalAlertDialogStatus by viewModel.generalAlertDialogStatus

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is InvoiceDetailViewModel.UIEvent.BackToTransactionHistoryScreen -> navController.navigate(Screen.TRANSACTION_HISTORY) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.background(color = Secondary100),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.ui.graphics.Color.Transparent,
                ),
                title = {
                    Column {
                        TextWithNoPadding(
                            "Invoice detail",
                            fontSize = 18.sp,
                            color = Secondary,
                            fontWeight = FontWeight.W500,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(4.dp))
                        TextWithNoPadding(
                            "Receipt summary",
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
                    /*
                    IconButton(onClick = { /* Options */ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = Secondary
                        )
                    }
                     */
                }
            )
        },
        floatingActionButton = {
            /*
            FloatingActionButton(
                onClick = { /* Add */ },
                containerColor = Secondary,
                contentColor = White,
                shape = CircleShape,
                modifier = Modifier.size(52.dp)
            ) {
                Text("+", fontSize = 28.sp, color = White, fontWeight = FontWeight.Light)
            }
             */
        }
    ) { innerPadding ->
        if (generalAlertDialogStatus.type == GeneralAlertDialogStatus.DialogType.SUCCESS) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 14.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Spacer(Modifier.height(2.dp))

                // ── Invoice Header Card
                InvoiceHeaderCard()

                // ── Purchased Items Section
                PurchasedItemsSection()

                // ── Payment Summary Section
                PaymentSummarySection()

                // ── Print Receipt Button
                PrintReceiptButton()

                Spacer(modifier = Modifier.height(80.dp))
            }
        } else if (generalAlertDialogStatus.type == GeneralAlertDialogStatus.DialogType.ERROR) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
            ) {
                // Icon ring
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Danger800)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Warning,
                        contentDescription = null,
                        tint = Danger400,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text = generalAlertDialogStatus.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W500,
                    color = Secondary,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = generalAlertDialogStatus.message,
                    fontSize = 14.sp,
                    color = Gray600,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Back to history
                    Button(
                        onClick = { viewModel.onEvent(InvoiceDetailEvent.OnClickBackToTransactionHistoryBtn) },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = White
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Back to history", fontSize = 14.sp, color = White)
                    }
                }
            }
        }

        if (generalAlertDialogStatus.showDialog)
            GeneralAlertDialog(
                generalAlertDialogStatus = generalAlertDialogStatus,
                onDismissRequest = { viewModel.onEvent(InvoiceDetailEvent.OnClickDismissGeneralDialogStatusBtn) },
            )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InvoiceDetailScreenPreview() {
    MaterialTheme {
        InvoiceDetailScreen(
            navController = rememberNavController()
        )
    }
}