package com.pos.cashiersp.presentation.cashier

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pos.cashiersp.presentation.ui.theme.Danger800
import com.pos.cashiersp.presentation.ui.theme.Dark
import com.pos.cashiersp.presentation.ui.theme.Gray100
import com.pos.cashiersp.presentation.ui.theme.Gray300
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary100
import com.pos.cashiersp.presentation.ui.theme.White
import java.util.concurrent.TimeUnit

@Composable
fun InformationDialog(
    isRefreshing: Boolean = false,
    viewModel: CashierViewModel
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val lastFetchedTimestamp = viewModel.lastUpdated.value
    val cartItemCount = viewModel.cart.value.size

    Dialog(
        onDismissRequest = { viewModel.onEvent(CashierEvent.OnToggleInfoBtn(false)) },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = White, shape = RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Information",
                    color = Dark,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
                /*
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { viewModel.onEvent(CashierEvent.OnToggleInfoBtn(false)) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close",
                        tint = Gray300
                    )
                }
                 */
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Last synced section
            Text(
                text = "LAST SYNCED",
                color = Gray300,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatLastFetched(lastFetchedTimestamp),
                    color = Dark,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                if (isRefreshing) {
                    CircularProgressIndicator(
                        color = Primary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    IconButton(
                        onClick = {
                            viewModel.onEvent(CashierEvent.RefreshCashierItem)
                            viewModel.onEvent(CashierEvent.OnToggleInfoBtn(false))
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Secondary100, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Refresh cache",
                            tint = Primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Secondary100)
            Spacer(modifier = Modifier.height(16.dp))

            // Cart section
            Text(
                text = "CART",
                color = Gray300,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))

            AnimatedContent(
                targetState = showDeleteConfirm,
                label = "cart-section",
                transitionSpec = { fadeIn() togetherWith fadeOut() }
            ) { confirming ->
                if (!confirming) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (cartItemCount == 1) "1 item" else "$cartItemCount items",
                            color = Dark,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { if (cartItemCount > 0) showDeleteConfirm = true },
                            enabled = cartItemCount > 0,
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    if (cartItemCount > 0) Gray100 else Secondary100,
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Clear cart",
                                tint = if (cartItemCount > 0) Danger800 else Gray300,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Remove all $cartItemCount items from cart?",
                            color = Dark,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showDeleteConfirm = false },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel", color = Dark)
                            }
                            Button(
                                onClick = {
                                    viewModel.onEvent(CashierEvent.OnDeleteAllCartItem)
                                    showDeleteConfirm = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD64545)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Remove", color = White)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            TextButton(
                onClick = { viewModel.onEvent(CashierEvent.OnToggleInfoBtn(false)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Done", color = Primary, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

private fun formatLastFetched(timestamp: Long?): String {
    if (timestamp == null) return "Never synced"
    val diffMs = System.currentTimeMillis() - timestamp
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMs)
    val hours = TimeUnit.MILLISECONDS.toHours(diffMs)
    val days = TimeUnit.MILLISECONDS.toDays(diffMs)
    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "$minutes min ago"
        hours < 24 -> "$hours h ago"
        days < 7 -> "$days d ago"
        else -> {
            val sdf = java.text.SimpleDateFormat("MMM d, HH:mm", java.util.Locale.getDefault())
            sdf.format(java.util.Date(timestamp))
        }
    }
}