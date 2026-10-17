package com.pos.cashiersp.presentation.cashier.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pos.cashiersp.common.TestTags
import com.pos.cashiersp.presentation.cashier.CashierViewModel
import com.pos.cashiersp.presentation.ui.theme.Gray100
import com.pos.cashiersp.presentation.ui.theme.Primary500
import com.pos.cashiersp.presentation.ui.theme.Secondary100

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashierPartialBottomSheet(
    onDismissRequest: () -> Unit,
    viewModel: CashierViewModel = hiltViewModel()
) {
    val transactionState by viewModel.transactionState
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true, // This will skip half state
        confirmValueChange = { newValue -> !transactionState.isLoading }
    )

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
