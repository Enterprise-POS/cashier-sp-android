package com.pos.cashiersp.presentation.cashier.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pos.cashiersp.common.TestTags
import com.pos.cashiersp.model.domain.CartItem
import com.pos.cashiersp.presentation.cashier.CashierViewModel
import com.pos.cashiersp.presentation.ui.theme.Gray100
import com.pos.cashiersp.presentation.ui.theme.Gray700
import com.pos.cashiersp.presentation.ui.theme.Primary500
import com.pos.cashiersp.presentation.ui.theme.Secondary100

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashierPartialBottomSheet(
    onDismissRequest: () -> Unit,
    viewModel: CashierViewModel = hiltViewModel()
) {
    val cart: Map<Int, CartItem> = viewModel.cart.value
    val staffName = viewModel.staffName.value
    val transactionState = viewModel.transactionState.value
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true, // This will skip half state
        confirmValueChange = { newValue -> !transactionState.isLoading }
    )
    var bottomSheetHeight by remember {
        mutableStateOf(0.dp)
    }

    /*
    * We could add new feature such as add customer info for continuous customer
    * */

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .fillMaxHeight()
            .background(Secondary100)
            .testTag(TestTags.CashierScreen.PAYMENT_BOTTOM_SHEET),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            FoodItemListContainer(
                modifier = Modifier
                    .padding(4.dp)
                    .weight(1f)
            )

            PaymentSummary(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(
                        color = Primary500.copy(alpha = .3f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .border(
                        width = .8.dp,
                        color = Gray100.copy(alpha = .2f),
                        shape = RoundedCornerShape(10.dp)
                    )
            )
        }
    }
}
