package com.pos.cashiersp.presentation.bluetooth_settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pos.cashiersp.presentation.global_component.TextWithNoPadding
import com.pos.cashiersp.presentation.ui.theme.Gray100
import com.pos.cashiersp.presentation.ui.theme.Gray300
import com.pos.cashiersp.presentation.ui.theme.Gray400
import com.pos.cashiersp.presentation.ui.theme.Gray600
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.Secondary100
import com.pos.cashiersp.presentation.ui.theme.Success
import com.pos.cashiersp.presentation.ui.theme.White

// Data class for Bluetooth devices
data class BluetoothDevice(
    val name: String,
    val description: String,
    val signalStrength: Int,
    val isConnected: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BluetoothSettingsScreen(
    navController: NavController,
    drawerState: DrawerState,
    viewModel: BluetoothSettingsViewModel = hiltViewModel()
) {
    val vmState = viewModel.vmState.value

    // Map ViewModel devices to UI devices
    val devices = vmState.devices.map { deviceInfo ->
        BluetoothDevice(
            name = deviceInfo.name,
            description = if (deviceInfo.isPaired) "Paired device" else "Available device",
            signalStrength = 75, // You can calculate this based on RSSI if needed
            isConnected = deviceInfo.isConnected
        )
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
                            "Select Device",
                            fontSize = 18.sp,
                            color = Secondary,
                            fontWeight = FontWeight.W500,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(6.dp))
                        TextWithNoPadding(
                            "Connect a Bluetooth printer or reader",
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
            )
        },
        modifier = Modifier.background(color = Secondary100)
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(innerPadding)
        ) {
            Spacer(Modifier.height(20.dp))
            Card(
                border = BorderStroke(width = .8.dp, color = Gray100.copy(alpha = .4f)),
                colors = CardDefaults.cardColors(containerColor = White),
                modifier = Modifier.padding(horizontal = 14.dp)
            ) {
                Column(
                    Modifier
                        .padding(horizontal = 14.dp, vertical = 18.dp)
                        .fillMaxWidth()
                ) {
                    TextWithNoPadding(
                        "Nearby devices",
                        fontSize = 16.sp,
                        color = Secondary,
                        fontWeight = FontWeight.W500,
                    )
                    Text(
                        "Tap a device to connect",
                        fontSize = 12.sp,
                        color = Gray400,
                        fontWeight = FontWeight.W400,
                    )
                    Spacer(Modifier.height(2.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .heightIn(max = 250.dp)
                            .padding(all = 4.dp)
                    ) {
                        if (vmState.isLoading) {
                            item {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    CircularProgressIndicator(
                                        color = Primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    Text("Loading devices...", color = Gray300)
                                }
                            }
                        } else if (devices.isEmpty()) {
                            item {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        "No devices found",
                                        color = Gray400,
                                        fontSize = 14.sp
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "Tap 'Scan again' to search for devices",
                                        color = Gray300,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        } else {
                            devices.forEach { device ->
                                item(key = device.name) {
                                    BluetoothDeviceItem(
                                        device = device,
                                        onClick = {
                                            // Find the actual device address from vmState
                                            val deviceInfo = vmState.devices.find { it.name == device.name }
                                            deviceInfo?.let { viewModel.connectToDevice(it.address) }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // No device you expect card
            Card(
                border = BorderStroke(width = .8.dp, color = Gray100.copy(alpha = .4f)),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(horizontal = 14.dp)
            ) {
                Column(
                    Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        "No device you expect ?",
                        fontSize = 16.sp,
                        color = Secondary,
                        fontWeight = FontWeight.W600,
                    )
                    Spacer(Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                White,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                "Make sure Bluetooth is on",
                                fontSize = 14.sp,
                                color = Color(0xFF1E293B),
                                fontWeight = FontWeight.W600,
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "Turn on Bluetooth and keep your printer or reader close to this device.",
                                fontSize = 13.sp,
                                color = Gray600,
                                lineHeight = 18.sp
                            )
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
                // Scan again button
                Button(
                    onClick = { /* Handle scan again */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9800) // Orange color
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        "Scan again",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W600,
                        color = White
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Use app without device button
                Button(
                    onClick = { /* Handle use without device */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE5E7EB)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        "Use app without device",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W500,
                        color = Color(0xFF1E293B)
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    "You can connect a Bluetooth device later from Settings.",
                    fontSize = 12.sp,
                    color = Gray400,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun BluetoothDeviceItem(
    device: BluetoothDevice,
    onClick: () -> Unit
) {
    Button(
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors()
            .copy(containerColor = Gray100.copy(alpha = .4f)),
        contentPadding = PaddingValues(0.dp),
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Left colored strip
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(56.dp)
                    .background(
                        color = if (device.isConnected) Success else Color.Transparent,
                        shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                    )
            )

            Spacer(Modifier.width(12.dp))

            // Bluetooth icon
            Card(
                colors = CardDefaults.cardColors().copy(containerColor = Primary),
                shape = RoundedCornerShape(6.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Bluetooth,
                    tint = White,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(20.dp),
                    contentDescription = "Bluetooth device icon",
                )
            }

            Spacer(Modifier.width(12.dp))

            // Device info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    device.name,
                    color = Secondary,
                    fontWeight = FontWeight.W500,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    device.description,
                    color = Gray400,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(Modifier.width(8.dp))

            // Signal strength and status
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Signal icon (you can replace with actual signal bars icon)
                    Text("📶", fontSize = 10.sp)
                    Spacer(Modifier.width(2.dp))
                    Text(
                        "${device.signalStrength}%",
                        fontSize = 12.sp,
                        color = Gray400,
                    )
                }
                Spacer(Modifier.height(2.dp))
                if (device.isConnected) {
                    Text(
                        "Connected",
                        fontSize = 11.sp,
                        color = Success,
                        fontWeight = FontWeight.W500,
                        modifier = Modifier
                            .background(
                                Success.copy(alpha = 0.1f),
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                } else {
                    Text(
                        "Tap to connect",
                        fontSize = 11.sp,
                        color = Primary,
                        fontWeight = FontWeight.W400
                    )
                }
            }
        }
    }
}