package com.pos.cashiersp.presentation.cashier.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pos.cashiersp.model.domain.CartItem
import com.pos.cashiersp.presentation.ui.theme.Gray300
import com.pos.cashiersp.presentation.ui.theme.Gray500
import com.pos.cashiersp.presentation.ui.theme.Gray600
import com.pos.cashiersp.presentation.ui.theme.Light900
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.White

@Composable
fun FoodItem(number: Int, cartItem: CartItem) {
    val quantity = cartItem.quantity
    val price = cartItem.storeStock.price
    val itemName = cartItem.item.itemName
    val priceFormatter = java.text.DecimalFormat("#,###")

    Card(
        border = BorderStroke(.4.dp, Gray500),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(horizontal = 14.dp)
        ) {

            // Number badge
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Secondary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number.toString(),
                    color = White,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.width(12.dp))

            // Item + quantity (take available space)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = itemName,
                    color = Secondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(2.dp))

                Row {
                    Text(
                        text = "x$quantity",
                        fontSize = 12.sp,
                        color = Gray600
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = "${cartItem.storeStock.price} / item",
                        fontSize = 12.sp,
                        color = Gray600
                    )
                }
            }

            // Price (right aligned)
            Text(
                text = "${priceFormatter.format(price * quantity)} 円",
                color = Secondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}