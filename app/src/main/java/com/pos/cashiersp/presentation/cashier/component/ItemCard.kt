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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ItemCard(cashierItem: CashierItem, viewModel: CashierViewModel = hiltViewModel()) {
    val cart: Map<Int, CartItem> = viewModel.cart.value
    var price = cashierItem.storeStockPrice.toDouble()
    val addedToCart = cart.containsKey(cashierItem.itemId)

    // If nothing found at cart then null
    val currentCartItemStatus = if (addedToCart) cart[cashierItem.itemId] else null

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
                Image(
                    painter = painterResource(id = R.drawable.noimage_compressed),
                    contentDescription = stringResource(id = R.string.enterprise_pos_logo),
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            // Content Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Title
                Text(
                    text = cashierItem.itemName,
                    fontSize = 12.sp,
                    color = Secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.height(20.dp),
                )

                // Size and Volume
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Later will be use as label
                    Text(
                        text = "ID",
                        fontSize = 10.sp,
                        color = Gray300,
                        fontWeight = FontWeight.W400,
                        style = LocalTextStyle.current.merge(
                            TextStyle(
                                lineHeight = 14.sp,
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                ),
                                lineHeightStyle = LineHeightStyle(
                                    alignment = LineHeightStyle.Alignment.Center,
                                    trim = LineHeightStyle.Trim.None
                                )
                            )
                        ),
                        modifier = Modifier
                            .height(12.dp)
                    )
                    Text(
                        text = " • ",
                        fontSize = 10.sp,
                        color = Gray300,
                        fontWeight = FontWeight.W400,
                        style = LocalTextStyle.current.merge(
                            TextStyle(
                                lineHeight = 14.sp,
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                ),
                                lineHeightStyle = LineHeightStyle(
                                    alignment = LineHeightStyle.Alignment.Center,
                                    trim = LineHeightStyle.Trim.None
                                )
                            )
                        ),
                        modifier = Modifier
                            .height(12.dp)
                    )
                    Text(
                        text = cashierItem.itemId.toString(),
                        fontSize = 10.sp,
                        color = Gray300,
                        fontWeight = FontWeight.W400,
                        style = LocalTextStyle.current.merge(
                            TextStyle(
                                lineHeight = 14.sp,
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                ),
                                lineHeightStyle = LineHeightStyle(
                                    alignment = LineHeightStyle.Alignment.Center,
                                    trim = LineHeightStyle.Trim.None
                                )
                            )
                        ),
                        modifier = Modifier
                            .height(12.dp)
                    )
                }

                // Price
                Text(
                    text = if (price > 0) "${String.format("%.2f", price)} 円" else "0 円",
                    fontSize = 12.sp,
                    color = Secondary
                )

                // Price and Add Button
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (addedToCart) {
                        // Minus Button
                        Button(
                            onClick = { viewModel.onEvent(CashierEvent.OnDecreaseQuantity(cashierItem, 1)) },
                            modifier = Modifier.size(18.dp),
                            enabled = currentCartItemStatus!!.quantity > 0,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Primary,
                                disabledContentColor = Gray300,
                                disabledContainerColor = Gray100
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.KeyboardArrowDown,
                                contentDescription = "Add to cart",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Text(
                            currentCartItemStatus.quantity.toString(),
                            fontSize = 14.sp,
                            color = Secondary
                        )

                        // Add Button
                        Button(
                            onClick = { viewModel.onEvent(CashierEvent.OnAddQuantity(cashierItem, 1)) },
                            modifier = Modifier.size(18.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = currentCartItemStatus.quantity <= 99,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Primary,
                                disabledContentColor = Gray300,
                                disabledContainerColor = Gray100
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.KeyboardArrowUp,
                                contentDescription = "Add to cart",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
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