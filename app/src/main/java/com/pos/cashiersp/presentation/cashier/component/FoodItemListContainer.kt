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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pos.cashiersp.model.domain.CartItem
import com.pos.cashiersp.presentation.cashier.CashierViewModel
import com.pos.cashiersp.presentation.ui.theme.Gray100
import com.pos.cashiersp.presentation.ui.theme.Gray700
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun FoodItemListContainer(
    modifier: Modifier,
    viewModel: CashierViewModel = hiltViewModel()
) {
    val cart: Map<Int, CartItem> = viewModel.cart.value
    val staffName = viewModel.staffName.value

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
        LazyVerticalGrid(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            columns = GridCells.Fixed(1),
            modifier = Modifier
                .nestedScroll(rememberNestedScrollInteropConnection())
                .fillMaxWidth()
                .weight(1f)
        ) {
            cart.onEachIndexed { index, (_, cartItem) -> item { FoodItem(index + 1, cartItem) } }
        }

        // This spacer makes room for bottom content
        //Spacer(modifier = Modifier.height(360.dp))
    }
}