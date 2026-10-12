package com.pos.cashiersp.presentation.select_tenant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.window.core.layout.WindowSizeClass
import com.pos.cashiersp.common.TestTags
import com.pos.cashiersp.model.dto.Tenant
import com.pos.cashiersp.presentation.Screen
import com.pos.cashiersp.presentation.select_tenant.component.AddNewTenantDialog
import com.pos.cashiersp.presentation.select_tenant.component.ErrorOnSelectTenantDialog
import com.pos.cashiersp.presentation.select_tenant.component.TenantActions
import com.pos.cashiersp.presentation.select_tenant.component.TenantHeading
import com.pos.cashiersp.presentation.select_tenant.component.TenantListCard
import com.pos.cashiersp.presentation.select_tenant.component.TryAgainDialog
import com.pos.cashiersp.presentation.ui.theme.Gray600
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.Secondary100
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SelectTenantScreen(
    navController: NavController,
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
    viewModel: SelectTenantViewModel = hiltViewModel()
) {
    val vmState = viewModel.state
    val tenantList = viewModel.tenantList.value
    val selectedTenant = viewModel.selectedTenant.value
    val openTryAgainDialog = viewModel.openTryAgainDialog.value
    val openErrorOnSelectTenant = viewModel.openErrorOnSelectTenant.value
    val openAddNewTenantDialog = viewModel.openAddNewTenantDialog.value
    // println(selectedTenant)

    LaunchedEffect(Unit) {
        viewModel.authorizationUIEvent.collectLatest { event ->
            when (event) {
                is SelectTenantViewModel.AuthorizationUIEvent.Logout -> navController.navigate(Screen.LOGIN_REGISTER) {
                    popUpTo(0) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.saveSelectedTenantUIEvent.collectLatest { event ->
            when (event) {
                is SelectTenantViewModel.SaveSelectedTenantUIEvent.ErrorWhileSelectTenant -> {
                    // _openTryAgain dialog will be handled AlertDialog
                }

                is SelectTenantViewModel.SaveSelectedTenantUIEvent.OkNavigateToSelectStore -> {
                    navController.navigate(Screen.SELECT_STORE + "/${event.tenantId}/${event.tenantName}")
                }
            }
        }
    }

    Scaffold(
        containerColor = Secondary100
    ) { innerPadding ->
        val isExpanded = windowSizeClass.isWidthAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND
        )
        if (isExpanded) {
            ExpandedSelectTenantLayout(
                isLoading = vmState.value.isLoading,
                tenantList = tenantList,
                selectedTenant = selectedTenant,
                onAddTenant = { viewModel.onEvent(SelectTenantEvent.SetOpenAddNewTenant(true)) },
                onContinue = { viewModel.onEvent(SelectTenantEvent.OnClickContinue) },
                onSwitchAccount = { viewModel.onEvent(SelectTenantEvent.OnClickSwitchAccount) },
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            CompactSelectTenantLayout(
                isLoading = vmState.value.isLoading,
                tenantList = tenantList,
                selectedTenant = selectedTenant,
                onAddTenant = { viewModel.onEvent(SelectTenantEvent.SetOpenAddNewTenant(true)) },
                onContinue = { viewModel.onEvent(SelectTenantEvent.OnClickContinue) },
                onSwitchAccount = { viewModel.onEvent(SelectTenantEvent.OnClickSwitchAccount) },
                modifier = Modifier.padding(innerPadding)
            )
        }

        if (openTryAgainDialog) {
            TryAgainDialog(
                onDismissRequest = { viewModel.onEvent(SelectTenantEvent.SetOpenTryAgainDialog(false)) },
                onConfirmation = { viewModel.onEvent(SelectTenantEvent.FetchTenantList) },
                dialogText = vmState.value.error ?: "Fatal Error: Could not identified causing error",
            )
        }
        if (openAddNewTenantDialog) {
            AddNewTenantDialog(
                onDismissRequest = { viewModel.onEvent(SelectTenantEvent.SetOpenAddNewTenant(false)) },
                onConfirmation = { viewModel.onEvent(SelectTenantEvent.NewTenantRequest) },
            )
        }

        if (openErrorOnSelectTenant) {
            ErrorOnSelectTenantDialog(
                onDismissRequest = { viewModel.onEvent(SelectTenantEvent.SetOpenErrorSelectTenantDialog(false)) },
                onConfirmation = { viewModel.onEvent(SelectTenantEvent.OnClickContinue) },
                dialogText = vmState.value.error ?: "Fatal Error: Could not identified causing error",
            )
        }
    }
}

@Composable
private fun CompactSelectTenantLayout(
    isLoading: Boolean,
    tenantList: List<Tenant>,
    selectedTenant: Tenant?,
    onAddTenant: () -> Unit,
    onContinue: () -> Unit,
    onSwitchAccount: () -> Unit,
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
            TenantHeading()
            Spacer(Modifier.height(20.dp))
            TenantListCard(isLoading, tenantList, onAddTenant)
        }
        TenantActions(
            isLoading = isLoading,
            hasSelectedTenant = selectedTenant != null,
            onContinue = onContinue,
            onSwitchAccount = onSwitchAccount,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        )
    }
}

@Composable
private fun ExpandedSelectTenantLayout(
    isLoading: Boolean,
    tenantList: List<Tenant>,
    selectedTenant: Tenant?,
    onAddTenant: () -> Unit,
    onContinue: () -> Unit,
    onSwitchAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp, vertical = 32.dp),
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
                Text(
                    "Select a tenant",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Secondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TestTags.SelectTenantScreen.SELECT_TENANT_TITLE)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Choose tenant that you want to manage",
                    fontSize = 15.sp,
                    color = Gray600,
                    lineHeight = 22.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TestTags.SelectTenantScreen.SELECT_TENANT_TITLE_SUB_TITLE)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .widthIn(max = 480.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TenantListCard(
                    isLoading = isLoading,
                    tenantList = tenantList,
                    onAddTenant = onAddTenant,
                    maxListHeight = 320.dp
                )
                Spacer(Modifier.height(12.dp))
                TenantActions(
                    isLoading = isLoading,
                    hasSelectedTenant = selectedTenant != null,
                    onContinue = onContinue,
                    onSwitchAccount = onSwitchAccount,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

