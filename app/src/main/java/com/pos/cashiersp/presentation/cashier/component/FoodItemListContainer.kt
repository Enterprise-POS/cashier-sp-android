package com.pos.cashiersp.presentation.cashier.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pos.cashiersp.presentation.cashier.CashierViewModel
import com.pos.cashiersp.presentation.ui.theme.Gray100
import com.pos.cashiersp.presentation.ui.theme.Gray700

@Composable
fun FoodItemListContainer(
    modifier: Modifier,
    viewModel: CashierViewModel = hiltViewModel()
) {
    val cart by viewModel.cart
    val staffName by viewModel.staffName
    val cartItems = remember(cart) { cart.entries.toList() }

    Column(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = Gray100.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp),
                )
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp)
            ) {
                Text(
                    "staff: $staffName",
                    color = Gray700,
                    fontSize = 12.sp,
                    style = TextStyle(
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.Both,
                        )
                    ),
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // List of foods
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            itemsIndexed(
                items = cartItems,
                key = { _, entry -> entry.key }
            ) { index, (_, cartItem) ->
                FoodItem(index + 1, cartItem)
            }
        }

        // This spacer makes room for bottom content
        //Spacer(modifier = Modifier.height(360.dp))
    }
}