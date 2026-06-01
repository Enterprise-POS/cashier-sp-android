package com.pos.cashiersp.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.StoreMallDirectory
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pos.cashiersp.presentation.Screen
import com.pos.cashiersp.presentation.global_component.TextWithNoPadding
import com.pos.cashiersp.presentation.settings.component.SettingsRow
import com.pos.cashiersp.presentation.settings.component.SettingsSectionCard
import com.pos.cashiersp.presentation.ui.theme.Gray400
import com.pos.cashiersp.presentation.ui.theme.Gray600
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Primary100
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.Secondary100
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel = hiltViewModel()) {
    val tenantName = viewModel.tenantName.value
    val storeName = viewModel.storeName.value

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is SettingsViewModel.UIEvent.ErrorAndMustNavigateToSelectTenantScreen -> navController.navigate(Screen.SELECT_TENANT) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }

                SettingsViewModel.UIEvent.NavigateToBluetoothSettings -> navController.navigate(Screen.BLUETOOTH_SETTINGS)

                SettingsViewModel.UIEvent.NavigateToSelectTenant -> navController.navigate(Screen.SELECT_TENANT)

                is SettingsViewModel.UIEvent.NavigateToSelectStore -> {
                    navController.navigate(Screen.SELECT_STORE + "/${event.tenantId}/${event.tenantName}")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
                title = {
                    Column {
                        TextWithNoPadding(
                            "Settings",
                            fontSize = 18.sp,
                            color = Secondary,
                            fontWeight = FontWeight.W500,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(6.dp))
                        TextWithNoPadding(
                            "Manage account and device preference",
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
                            contentDescription = "Back to previous screen",
                            tint = Secondary
                        )
                    }
                },
            )
        },
        modifier = Modifier.background(color = Secondary100)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            /*
            // Account section
            SettingsSectionCard(
                title = "Account",
                subtitle = "Profile and sign-in details.",
                accentColor = Primary
            ) {
                SettingsRow(
                    icon = Icons.Default.Person,
                    label = "User profile",
                    description = "View and edit personal info & roles.",
                    iconBgColor = Primary100,
                    iconTint = Primary,
                    badge = "Owner",
                    badgeBgColor = Primary100,
                    badgeTextColor = Primary,
                    onClick = {}
                )
            }
             */

            // Devices section
            SettingsSectionCard(
                title = "Devices",
                subtitle = "Printers and Bluetooth devices.",
                accentColor = Primary
            ) {
                SettingsRow(
                    icon = Icons.Default.Print,
                    label = "Printer settings",
                    description = "Select default printer and receipts.",
                    iconBgColor = Primary100,
                    iconTint = Primary,
                    badge = "View",
                    badgeBgColor = Primary100,
                    badgeTextColor = Primary,
                    onClick = { viewModel.onEvent(SettingsEvent.OnClickPrinterSettings) }
                )
            }

            // Tenant & Store section
            SettingsSectionCard(
                title = "Tenant & Store",
                subtitle = "Quickly switch where you are working.",
                accentColor = Primary
            ) {
                SettingsRow(
                    icon = Icons.Default.SwitchAccount,
                    label = "Select tenant",
                    description = "Current: $tenantName.",
                    iconBgColor = Primary100,
                    iconTint = Primary,
                    actionLabel = "Change",
                    onClick = { viewModel.onEvent(SettingsEvent.OnClickSelectTenant) }
                )
                Spacer(Modifier.height(8.dp))
                SettingsRow(
                    icon = Icons.Default.StoreMallDirectory,
                    label = "Select store",
                    description = "Current: $storeName.",
                    iconBgColor = Primary100,
                    iconTint = Primary,
                    actionLabel = "Change",
                    onClick = { viewModel.onEvent(SettingsEvent.OnClickSelectStore) }
                )
            }

            // Caption
            Text(
                text = "More options can be added here in the future.",
                fontSize = 11.sp,
                color = Gray400,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}
