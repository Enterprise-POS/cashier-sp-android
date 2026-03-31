package com.pos.cashiersp.presentation.cashier.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.pos.cashiersp.R
import com.pos.cashiersp.model.domain.CartItem
import com.pos.cashiersp.model.dto.CashierItem
import com.pos.cashiersp.presentation.cashier.CashierEvent
import com.pos.cashiersp.presentation.cashier.CashierViewModel
import com.pos.cashiersp.presentation.ui.theme.Gray100
import com.pos.cashiersp.presentation.ui.theme.Gray300
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.White

@Composable
fun ItemCard(cashierItem: CashierItem, viewModel: CashierViewModel) {
    val cart: Map<Int, CartItem> = viewModel.cart.value
    var price = cashierItem.storeStockPrice.toDouble()
    val currentCartItemStatus: CartItem? = remember(cart, cashierItem.itemId) {
        cart[cashierItem.itemId]
    }
    val addedToCart = currentCartItemStatus != null

    val compactTextStyle = LocalTextStyle.current.merge(
        TextStyle(
            lineHeight = 14.sp,
            platformStyle = PlatformTextStyle(includeFontPadding = false),
            lineHeightStyle = LineHeightStyle(
                alignment = LineHeightStyle.Alignment.Center,
                trim = LineHeightStyle.Trim.None
            )
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            // Coffee Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                contentAlignment = Alignment.Center
            ) {
                /*
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = "https://images.unsplash.com/photo-1541167760496-1628856ab772?q=80&w=125&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                    contentDescription = null,
                    fallback = painterResource(id = R.drawable.noimage_compressed)
                )
                 */
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(R.drawable.noimage_compressed)
                        .size(120, 120)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Product image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Content Section
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = cashierItem.itemName,
                    fontSize = 12.sp,
                    color = Secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.height(20.dp),
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    listOf("ID", " • ", cashierItem.itemId.toString()).forEach { label ->
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            color = Gray300,
                            fontWeight = FontWeight.W400,
                            style = compactTextStyle,
                            modifier = Modifier.height(12.dp)
                        )
                    }
                }

                Text(
                    text = if (price > 0) "Rp ${"%.2f".format(price)}" else "Rp 0",
                    fontSize = 12.sp,
                    color = Secondary
                )

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (addedToCart && currentCartItemStatus != null) {
                        CartButton(
                            onClick = { viewModel.onEvent(CashierEvent.OnDecreaseQuantity(cashierItem, 1)) },
                            enabled = currentCartItemStatus.quantity > 0,
                            icon = Icons.Outlined.KeyboardArrowDown,
                            contentDescription = "Decrease quantity"
                        )

                        Text(
                            currentCartItemStatus.quantity.toString(),
                            fontSize = 14.sp,
                            color = Secondary
                        )

                        CartButton(
                            onClick = { viewModel.onEvent(CashierEvent.OnAddQuantity(cashierItem, 1)) },
                            enabled = currentCartItemStatus.quantity <= 99,
                            icon = Icons.Outlined.KeyboardArrowUp,
                            contentDescription = "Increase quantity"
                        )
                    } else {
                        TextButton(
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp),
                            onClick = { viewModel.onEvent(CashierEvent.OnAddToCart(cashierItem)) }
                        ) {
                            Text("Add to cart", fontSize = 8.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CartButton(
    onClick: () -> Unit,
    enabled: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(18.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
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
            modifier = Modifier.size(32.dp)
        )
    }
}