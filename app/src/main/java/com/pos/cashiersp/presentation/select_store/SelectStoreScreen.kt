package com.pos.cashiersp.presentation.select_store

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
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.window.core.layout.WindowSizeClass
import com.pos.cashiersp.model.domain.Store
import com.pos.cashiersp.presentation.Screen
import com.pos.cashiersp.presentation.select_store.components.ErrorAlert
import com.pos.cashiersp.presentation.select_store.components.StoreCard
import com.pos.cashiersp.presentation.ui.theme.Gray300
import com.pos.cashiersp.presentation.ui.theme.Gray400
import com.pos.cashiersp.presentation.ui.theme.Gray600
import com.pos.cashiersp.presentation.ui.theme.Light300
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.White
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SelectStoreScreen(
    tenantId: Int,
    tenantName: String,
    navController: NavController,
    viewModel: SelectStoreViewModel = hiltViewModel(),
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
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

    Scaffold(containerColor = Light300) { innerPadding ->
        val isExpanded = windowSizeClass.isWidthAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND
        )
        Box(modifier = Modifier.padding(innerPadding)) {
            if (isExpanded) {
                ExpandedSelectStoreLayout(
                    tenantName = tenantName,
                    isLoading = vmState.isLoading,
                    storeList = storeList,
                    selectedStore = selectedStore != null,
                    onContinue = { viewModel.onEvent(SelectStoreEvent.OnContinueButtonClicked) },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                CompactSelectStoreLayout(
                    tenantName = tenantName,
                    isLoading = vmState.isLoading,
                    storeList = storeList,
                    selectedStore = selectedStore != null,
                    onContinue = { viewModel.onEvent(SelectStoreEvent.OnContinueButtonClicked) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp),
                onClick = { viewModel.onEvent(SelectStoreEvent.OnBackButtonClicked) }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to select tenant screen"
                )
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

@Composable
private fun CompactSelectStoreLayout(
    tenantName: String,
    isLoading: Boolean,
    storeList: List<Store>,
    selectedStore: Boolean,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            StoreHeading()
            Spacer(Modifier.height(20.dp))
            StoreSelectionPanel(tenantName, isLoading, storeList)
        }
        StoreActions(
            isLoading = isLoading,
            hasSelectedStore = selectedStore,
            onContinue = onContinue,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.Bottom
        )
    }
}

@Composable
private fun ExpandedSelectStoreLayout(
    tenantName: String,
    isLoading: Boolean,
    storeList: List<Store>,
    selectedStore: Boolean,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(horizontal = 48.dp, vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 1040.dp),
            horizontalArrangement = Arrangement.spacedBy(48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                StoreHeading(expanded = true)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .widthIn(max = 480.dp)
            ) {
                StoreSelectionPanel(tenantName, isLoading, storeList)
                Spacer(Modifier.height(12.dp))
                StoreActions(isLoading, selectedStore, onContinue)
            }
        }
    }
}

@Composable
private fun StoreHeading(expanded: Boolean = false) {
    Text(
        "Choose store",
        fontSize = if (expanded) 36.sp else 28.sp,
        color = Secondary,
        fontWeight = if (expanded) FontWeight.Bold else FontWeight.Normal,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(if (expanded) 8.dp else 0.dp))
    Text(
        "Select where are you selling today",
        fontSize = if (expanded) 15.sp else 12.sp,
        color = Gray600,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun StoreSelectionPanel(
    tenantName: String,
    isLoading: Boolean,
    storeList: List<Store>
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = White)
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
                if (isLoading) {
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Primary, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.height(6.dp))
                            Text("Loading all $tenantName stores...", color = Gray300)
                        }
                    }
                } else if (storeList.isNotEmpty()) {
                    storeList.forEach { store -> item { StoreCard(store) } }
                } else {
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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

@Composable
private fun StoreActions(
    isLoading: Boolean,
    hasSelectedStore: Boolean,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = verticalArrangement
    ) {
        TextButton(
            enabled = !isLoading && hasSelectedStore,
            colors = ButtonDefaults.textButtonColors(
                containerColor = Primary,
                contentColor = White
            ),
            shape = RoundedCornerShape(corner = CornerSize(12.dp)),
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp),
            onClick = onContinue,
        ) {
            Text("Continue to POS")
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "You can change store later from Setting > Select Store",
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(10.dp))
    }
}
