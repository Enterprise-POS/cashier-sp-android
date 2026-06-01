package com.pos.cashiersp.presentation.stock_management.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pos.cashiersp.R
import com.pos.cashiersp.model.dto.StockType
import com.pos.cashiersp.model.dto.StoreStockV2
import com.pos.cashiersp.presentation.stock_management.StockManagementEvent
import com.pos.cashiersp.presentation.stock_management.StockManagementViewModel
import com.pos.cashiersp.presentation.ui.theme.Gray100
import com.pos.cashiersp.presentation.ui.theme.Gray400
import com.pos.cashiersp.presentation.ui.theme.Gray500
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.Success
import com.pos.cashiersp.presentation.ui.theme.White
import com.pos.cashiersp.presentation.util.dateFormatter
import com.pos.cashiersp.presentation.util.parseDateString
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailBottomSheet(
    viewModel: StockManagementViewModel = hiltViewModel()
) {
    val openDetailStockDialog by viewModel.openDetailStockDialog
    val selectedItem: StoreStockV2? = viewModel.selectedDetailStockDialog.value

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.uiBottomSheet.collectLatest { event ->
            when (event) {
                is StockManagementViewModel.UIBottomSheet.ShowModalBottomSheet -> {
                    if (event.show) {
                        sheetState.show()
                    } else {
                        sheetState.hide()
                    }
                }
            }
        }
    }

    if (openDetailStockDialog.showDialog) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onEvent(StockManagementEvent.OnTapCloseDetailsBottomSheet) },
            sheetState = sheetState,
            containerColor = White,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            dragHandle = null,                      // we handle our own close button
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (selectedItem != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 32.dp),
                ) {
                    // ── Item header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Emoji tile
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Gray100,
                            modifier = Modifier.size(52.dp),
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.noimage_compressed),
                                contentDescription = stringResource(id = R.string.enterprise_pos_logo),
                                modifier = Modifier
                                    .fillMaxSize()
                            )
                        }

                        Spacer(Modifier.width(12.dp))

                        // Name + subtitle
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedItem.itemName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.W700,
                                color = Secondary,
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "ID ${selectedItem.id} · ${selectedItem.stockType.name}",
                                fontSize = 12.sp,
                                color = Gray500,
                                fontWeight = FontWeight.W400,
                            )
                        }

                        Spacer(Modifier.width(12.dp))

                        // Close (X) button
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Secondary)
                                .clickable { viewModel.onEvent(StockManagementEvent.OnTapCloseDetailsBottomSheet) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = White,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }

                    HorizontalDivider(color = Gray100, thickness = 0.8.dp)

                    // ── Stock type
                    DetailRow(
                        title = "Stock type",
                        subtitle = if (selectedItem.stockType == StockType.TRACKED) "Stock tracking is enabled" else "Stock tracking is disabled"
                    ) {
                        StockTypeToggle(isTracked = selectedItem.stockType == StockType.TRACKED)
                    }

                    RowDivider()

                    // Current stock
                    DetailRow(
                        title = "Current stock",
                        subtitle = "Quantity available for sale",
                    ) {
                        Text(
                            text = selectedItem.stocks.toString(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W600,
                            color = Secondary,
                        )
                    }

                    RowDivider()

                    // Store price
                    DetailRow(
                        title = "Price",
                        subtitle = "Price from current store",
                    ) {
                        Text(
                            text = "Rp ${"%.2f".format(selectedItem.price.toDouble())}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W600,
                            color = Secondary,
                        )
                    }

                    RowDivider()

                    // ── Status
                    DetailRow(
                        title = "Status",
                        subtitle = "Status of this product if it's can be sold",
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (selectedItem.isActive) Success else Gray400,
                        ) {
                            Text(
                                text = if (selectedItem.isActive) "Active" else "Inactive",
                                color = White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.W600,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            )
                        }
                    }

                    RowDivider()

                    // Created at
                    DetailRow(
                        title = "Created at",
                        subtitle = "Read-only audit info",
                    ) {
                        Text(
                            text = dateFormatter(parseDateString(selectedItem.createdAt), "dd MMM yyyy - HH:mm"),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.W500,
                            color = Secondary,
                        )
                    }


                    // ── Info banner
                    /*
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Gray400)
                            .border(0.8.dp, Gray100, RoundedCornerShape(10.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                    ) {
                        Text(
                            text = "Changes to stock, base price, and status will apply immediately on all POS devices and channels.",
                            fontSize = 12.sp,
                            color = Color(0xFF8D6E34),
                            lineHeight = 18.sp,
                        )
                    }
                     */

                    // ── Action buttons
                    /*
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        // Deactivate item
                        Button(
                            onClick = onDeactivate,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                        ) {
                            Text(
                                text = "Deactivate item",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.W700,
                                color = White,
                            )
                        }

                        // Save changes
                        Button(
                            onClick = { onSaveChanges(isTracked, stock) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                        ) {
                            Text(
                                text = "Save changes",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.W700,
                                color = White,
                            )
                        }
                    }
                     */
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                ) {
                    Text("Error, nothing to show", color = Gray400, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

// ─── Reusable row layout

@Composable
private fun DetailRow(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    trailing: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.W600,
                color = Secondary,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                fontWeight = FontWeight.W400,
                color = Gray500,
            )
        }
        Spacer(Modifier.width(16.dp))
        trailing()
    }
}

@Composable
private fun RowDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 20.dp),
        thickness = 0.6.dp,
        color = Gray100,
    )
}


// ─── Stock stepper (− 42 +)

@Composable
private fun StockStepper(value: Int, onDecrement: () -> Unit, onIncrement: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        // − button
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                .border(0.8.dp, Gray100, RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                .clickable { onDecrement() },
            contentAlignment = Alignment.Center,
        ) {
            Text("−", fontSize = 18.sp, color = Secondary, fontWeight = FontWeight.W300)
        }

        // Value
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(34.dp)
                .border(BorderStroke(0.8.dp, Gray100)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "$value",
                fontSize = 15.sp,
                fontWeight = FontWeight.W600,
                color = Secondary,
            )
        }

        // + button
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                .border(0.8.dp, Gray100, RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                .clickable { onIncrement() },
            contentAlignment = Alignment.Center,
        ) {
            Text("+", fontSize = 18.sp, color = Secondary, fontWeight = FontWeight.W300)
        }
    }
}

// ─── Stock type toggle (TRACKED | ON)
@Composable
private fun StockTypeToggle(isTracked: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // "TRACKED" / "UNLIMITED" label chip
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = Gray100.copy(alpha = 0.6f),
        ) {
            Text(
                text = if (isTracked) "TRACKED" else "UNLIMITED",
                fontSize = 11.sp,
                fontWeight = FontWeight.W700,
                color = Secondary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }

        // ON/OFF switch
        Switch(
            checked = isTracked,
            onCheckedChange = { /* Do nothing */ },
            colors = SwitchDefaults.colors(
                checkedThumbColor = White,
                checkedTrackColor = Success,
                uncheckedThumbColor = White,
                uncheckedTrackColor = Gray100,
            ),
            modifier = Modifier
                .height(24.dp)
                .width(48.dp),
        )
    }
}
