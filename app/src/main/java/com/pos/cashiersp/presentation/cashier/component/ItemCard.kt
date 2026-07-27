package com.pos.cashiersp.presentation.cashier.component

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pos.cashiersp.model.domain.CartItem
import com.pos.cashiersp.model.dto.CashierItem
import com.pos.cashiersp.presentation.cashier.CashierEvent
import com.pos.cashiersp.presentation.cashier.CashierViewModel
import com.pos.cashiersp.presentation.ui.theme.Gray100
import com.pos.cashiersp.presentation.ui.theme.Gray300
import com.pos.cashiersp.presentation.ui.theme.Gray500
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.White
import com.pos.cashiersp.presentation.util.toRupiah

@Composable
fun ItemCard(cashierItem: CashierItem, viewModel: CashierViewModel) {
    val context = LocalContext.current
    val price = cashierItem.storeStockPrice.toDouble()
    val cart = viewModel.cart.value

    val currentCartItemStatus: CartItem? = remember(cart, cashierItem.itemId) {
        cart[cashierItem.itemId]
    }
    val addedToCart = currentCartItemStatus != null

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, Gray100, RoundedCornerShape(16.dp)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = cashierItem.itemName,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Secondary,
                maxLines = 2,
                lineHeight = 15.sp,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .clickable {
                        Toast.makeText(context, cashierItem.itemName, Toast.LENGTH_SHORT).show()
                    }
            )

            Spacer(Modifier.height(2.dp))

            // Item ID
            Text(
                text = "ID • ${cashierItem.itemId}",
                fontSize = 10.sp,
                color = Gray500,
                fontWeight = FontWeight.W400,
            )

            Spacer(Modifier.height(6.dp))

            // Price
            Text(
                text = if (price > 0) price.toRupiah() else "Rp 0",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )

            Spacer(Modifier.height(10.dp))

            // Action row: either quantity stepper or "Add to cart".
            // Fixed height here keeps the card the same size in both states.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                contentAlignment = Alignment.Center
            ) {
                if (addedToCart) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CartButton(
                            onClick = { viewModel.onEvent(CashierEvent.OnDecreaseQuantity(cashierItem, 1)) },
                            enabled = currentCartItemStatus.quantity > 0,
                            icon = Icons.Outlined.KeyboardArrowDown,
                            contentDescription = "Decrease quantity"
                        )

                        Text(
                            currentCartItemStatus.quantity.toString(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Secondary
                        )

                        CartButton(
                            onClick = { viewModel.onEvent(CashierEvent.OnAddQuantity(cashierItem, 1)) },
                            enabled = currentCartItemStatus.quantity <= 99,
                            icon = Icons.Outlined.KeyboardArrowUp,
                            contentDescription = "Increase quantity"
                        )
                    }
                } else {
                    Button(
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        modifier = Modifier.fillMaxSize(),
                        onClick = { viewModel.onEvent(CashierEvent.OnAddToCart(cashierItem)) }
                    ) {
                        Text(
                            "Add to cart",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = White
                        )
                    }
                }
            }
        }
    }
}

/**
 * Small quantity stepper button (+/-).
 */
@Composable
private fun CartButton(
    onClick: () -> Unit,
    enabled: Boolean,
    icon: ImageVector,
    contentDescription: String
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(40.dp),
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary,
            disabledContentColor = Gray300,
            disabledContainerColor = Gray100
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
    }
}