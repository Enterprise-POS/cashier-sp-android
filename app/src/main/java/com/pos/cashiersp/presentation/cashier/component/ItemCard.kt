package com.pos.cashiersp.presentation.cashier.component

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pos.cashiersp.model.domain.CartItem
import com.pos.cashiersp.model.dto.CashierItem
import com.pos.cashiersp.presentation.cashier.CashierEvent
import com.pos.cashiersp.presentation.cashier.CashierViewModel
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.util.toRupiah

@Composable
fun ItemCard(cashierItem: CashierItem, viewModel: CashierViewModel) {
    val context = LocalContext.current
    val cart = viewModel.cart.value
    val currentCartItemStatus: CartItem? = remember(cart, cashierItem.itemId) {
        cart[cashierItem.itemId]
    }
    val addedToCart = currentCartItemStatus != null

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val compactCard = maxWidth < 170.dp

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(16.dp)
                ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(if (compactCard) 8.dp else 16.dp),
                verticalArrangement = Arrangement.spacedBy(if (compactCard) 6.dp else 8.dp),
            ) {
                Text(
                    text = cashierItem.itemName,
                    style = if (compactCard) {
                        MaterialTheme.typography.titleSmall
                    } else {
                        MaterialTheme.typography.titleMedium
                    },
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = if (compactCard) 40.dp else 48.dp)
                        .clickable {
                            Toast.makeText(context, cashierItem.itemName, Toast.LENGTH_SHORT).show()
                        }
                )

                Text(
                    text = "ID • ${cashierItem.itemId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Text(
                    text = cashierItem.storeStockPrice.toRupiah(),
                    style = if (compactCard) {
                        MaterialTheme.typography.titleSmall
                    } else {
                        MaterialTheme.typography.titleMedium
                    },
                    color = MaterialTheme.colorScheme.secondary,
                )

                if (addedToCart) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = if (compactCard) 40.dp else 48.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CartButton(
                            compact = compactCard,
                            onClick = {
                                viewModel.onEvent(CashierEvent.OnDecreaseQuantity(cashierItem, 1))
                            },
                            enabled = currentCartItemStatus.quantity > 0,
                            icon = Icons.Outlined.KeyboardArrowDown,
                            contentDescription = "Decrease quantity"
                        )

                        Text(
                            text = currentCartItemStatus.quantity.toString(),
                            style = if (compactCard) {
                                MaterialTheme.typography.titleSmall
                            } else {
                                MaterialTheme.typography.titleMedium
                            },
                            color = MaterialTheme.colorScheme.onSurface,
                        )

                        CartButton(
                            compact = compactCard,
                            onClick = {
                                viewModel.onEvent(CashierEvent.OnAddQuantity(cashierItem, 1))
                            },
                            enabled = currentCartItemStatus.quantity < 999,
                            icon = Icons.Outlined.KeyboardArrowUp,
                            contentDescription = "Increase quantity"
                        )
                    }
                } else {
                    Button(
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(
                            horizontal = if (compactCard) 2.dp else 12.dp,
                            vertical = 4.dp
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = Secondary,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = if (compactCard) 40.dp else 48.dp),
                        onClick = { viewModel.onEvent(CashierEvent.OnAddToCart(cashierItem)) }
                    ) {
                        Text(
                            text = "Add to cart",
                            style = if (compactCard) {
                                MaterialTheme.typography.labelSmall
                            } else {
                                MaterialTheme.typography.labelLarge
                            },
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CartButton(
    compact: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(if (compact) 32.dp else 48.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (enabled) Primary else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (enabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) Color.White else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.size(if (compact) 18.dp else 24.dp),
        )
    }
}
