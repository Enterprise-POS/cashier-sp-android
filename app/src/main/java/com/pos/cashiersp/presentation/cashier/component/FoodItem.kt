package com.pos.cashiersp.presentation.cashier.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pos.cashiersp.model.domain.CartItem
import com.pos.cashiersp.presentation.ui.theme.Gray300
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
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Secondary), // dark background
        modifier = Modifier.fillMaxWidth(1f),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
                .height(52.dp),
        ) {
            // Circle number
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Primary, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number.toString(),
                    color = White,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Item + quantity - using weight to take available space
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    itemName,
                    color = White,
                    style = TextStyle(fontSize = 12.sp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "x${quantity}",
                    style = TextStyle(fontSize = 12.sp),
                    color = Gray300
                )
            }

            // Price
            Text(
                text = "${priceFormatter.format(price * quantity)} 円",
                color = White,
                style = TextStyle(fontSize = 12.sp)
            )
        }
    }
}