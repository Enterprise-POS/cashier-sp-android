package com.pos.cashiersp.presentation.select_store

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pos.cashiersp.presentation.Screen
import com.pos.cashiersp.presentation.select_store.components.ErrorAlert
import com.pos.cashiersp.presentation.select_store.components.StoreCard
import com.pos.cashiersp.presentation.ui.theme.Gray300
import com.pos.cashiersp.presentation.ui.theme.Gray400
import com.pos.cashiersp.presentation.ui.theme.Gray600
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.Secondary100
import com.pos.cashiersp.presentation.ui.theme.White
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectStoreScreen(
    tenantId: Int,
    tenantName: String,
    navController: NavController, viewModel: SelectStoreViewModel = hiltViewModel()
) {
    val vmState = viewModel.state.value
    val storeList = viewModel.storeList.value
    val selectedStore = viewModel.selectedStore.value
    val openTryAgainAlert = viewModel.openErrorAlert.value

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                SelectStoreViewModel.UIEvent.BackToSelectTenant -> navController.popBackStack()

                SelectStoreViewModel.UIEvent.ContinueToCashier -> navController.navigate(Screen.CASHIER)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(SelectStoreEvent.OnBackButtonClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to select tenant screen"
                        )
                    }
                },
            )
        },
        modifier = Modifier
            .background(color = Secondary100)
    ) { innerPadding ->
        Box {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp)
            ) {
                Text(
                    "Choose store",
                    fontSize = 28.sp,
                    color = Secondary,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Text(
                    "Select where are you selling today",
                    fontSize = 12.sp,
                    color = Gray600,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(Modifier.height(20.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = White
                    ),
                ) {
                    Column(
                        Modifier
                            .padding(horizontal = 14.dp, vertical = 18.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            "All stores",
                            fontSize = 16.sp,
                            color = Secondary,
                            fontWeight = FontWeight.W400,
                        )
                        Row {
                            Text(
                                "View every store under",
                                fontSize = 12.sp,
                                color = Gray400,
                                fontWeight = FontWeight.W400,
                            )
                            Text(
                                " $tenantName",
                                fontSize = 12.sp,
                                color = Primary,
                                fontWeight = FontWeight.W400
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(1),
                            modifier = Modifier.heightIn(max = 250.dp)
                        ) {
                            if (vmState.isLoading) {
                                item {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        CircularProgressIndicator(color = Primary, modifier = Modifier.size(24.dp))
                                        Spacer(Modifier.height(6.dp))
                                        Text(
                                            "Loading all $tenantName stores...",
                                            color = Gray300,
                                        )
                                    }
                                }
                            } else {
                                if (storeList.isNotEmpty())
                                    storeList.forEach { item { StoreCard(it) } }
                                else
                                    item {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Spacer(Modifier.height(6.dp))
                                            Text(
                                                "No store found yet. Create store by using Enterprise POS web application",
                                                color = Gray300,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                            }
                        }
                    }
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
            ) {
                TextButton(
                    enabled = !vmState.isLoading && selectedStore != null,
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Primary,
                        contentColor = White
                    ),
                    shape = RoundedCornerShape(corner = CornerSize(12.dp)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    onClick = { viewModel.onEvent(SelectStoreEvent.OnContinueButtonClicked) },
                ) {
                    Text("Continue to POS")
                }
                Spacer(Modifier.height(12.dp))
                Text("You can change store later from Setting > Select Store", fontSize = 12.sp)
                Spacer(Modifier.height(32.dp))
            }
        }
    }

    if (openTryAgainAlert) {
        ErrorAlert(
            onDismissRequest = { viewModel.onEvent(SelectStoreEvent.OnCloseErrorAlert) },
            onConfirmation = { viewModel.onEvent(SelectStoreEvent.OnTryAgainFetchStoreList) },
            confirmationText = "Try Again",
            dialogTitle = "Couldn't fetch data",
            dialogText = vmState.error ?: "Unknown reason occurred"
        )
    }
}