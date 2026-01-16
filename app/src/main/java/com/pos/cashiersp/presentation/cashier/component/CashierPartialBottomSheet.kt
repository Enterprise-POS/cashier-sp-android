package com.pos.cashiersp.presentation.cashier.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import com.pos.cashiersp.R
import com.pos.cashiersp.common.TestTags
import com.pos.cashiersp.model.domain.CartItem
import com.pos.cashiersp.presentation.cashier.CashierEvent
import com.pos.cashiersp.presentation.cashier.CashierViewModel
import com.pos.cashiersp.presentation.ui.theme.Danger900
import com.pos.cashiersp.presentation.ui.theme.Gray100
import com.pos.cashiersp.presentation.ui.theme.Gray200
import com.pos.cashiersp.presentation.ui.theme.Gray300
import com.pos.cashiersp.presentation.ui.theme.Gray500
import com.pos.cashiersp.presentation.ui.theme.Gray700
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Primary100
import com.pos.cashiersp.presentation.ui.theme.Primary500
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.Secondary100
import com.pos.cashiersp.presentation.ui.theme.White
import com.pos.cashiersp.presentation.util.PaymentMethod
import com.pos.cashiersp.presentation.util.ThousandsSeparatorTransformation
import java.text.DecimalFormat

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashierPartialBottomSheet(
    onDismissRequest: () -> Unit,
    viewModel: CashierViewModel = hiltViewModel()
) {
    val cart: Map<Int, CartItem> = viewModel.cart.value
    val staffName = viewModel.staffName.value
    val selectedPaymentMethod = viewModel.selectedPaymentMethod.value
    val inpCashPaymentMethod = viewModel.inpCashPaymentMethod.value
    val transactionState = viewModel.transactionState.value
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true, // This will skip half state
        confirmValueChange = { newValue -> !transactionState.isLoading }
    )

    val priceFormatter = DecimalFormat("#,###")

    /*
    * We could add new feature such as add customer info for continuous customer
    * */

    val subTotal = cart.entries.fold(initial = 0) { acc, (_, cartItem) ->
        acc + (cartItem.storeStock.price * cartItem.quantity)
    }

    // Feature for sumDiscount (not implemented)
    val sumDiscount: Float = 0f
    val total = subTotal + sumDiscount

    ModalBottomSheet(
        sheetState = sheetState,
        modifier = Modifier
            .fillMaxHeight()
            .background(Secondary100)
            .testTag(TestTags.CashierScreen.PAYMENT_BOTTOM_SHEET),
        onDismissRequest = onDismissRequest,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = Gray100.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(8.dp),
                        )
                        .fillMaxWidth()
                        .height(68.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp)
                    ) {
                        Text(
                            "Order no. 77",
                            color = Secondary,
                            fontSize = 18.sp,
                            style = LocalTextStyle.current.merge(
                                TextStyle(
                                    fontWeight = FontWeight.W500,
                                    lineHeightStyle = LineHeightStyle(
                                        alignment = LineHeightStyle.Alignment.Center,
                                        trim = LineHeightStyle.Trim.Both,
                                    )
                                ),
                            ),
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "staff: $staffName",
                            color = Gray700,
                            fontSize = 12.sp,
                            style = LocalTextStyle.current.merge(
                                TextStyle(
                                    fontWeight = FontWeight.W500,
                                    lineHeightStyle = LineHeightStyle(
                                        alignment = LineHeightStyle.Alignment.Center,
                                        trim = LineHeightStyle.Trim.Both,
                                    )
                                ),
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
                    /*
                    itemChart.entries.toList().forEachIndexed { index: Int, (itemId: String, quantity: Int) ->
                        val itemRef: String? = "item value"
                        if (itemRef != null) {
                            item { FoodItem(index + 1, itemRef, quantity) }
                        }
                    }
                    */
                    cart.onEachIndexed { index, (_, cartItem) -> item { FoodItem(index + 1, cartItem) } }
                }

                // This spacer makes room for bottom content
                Spacer(modifier = Modifier.height(360.dp))
            }

            // Bottom column with fixed height
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(372.dp)
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
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .padding(start = 14.dp, end = 14.dp, top = 14.dp)
                        .fillMaxWidth(),
                ) {
                    Text(
                        "Subtotal",
                        color = Gray500,
                        fontSize = 14.sp,
                        style = LocalTextStyle.current.merge(
                            TextStyle(
                                fontWeight = FontWeight.Normal,
                                lineHeightStyle = LineHeightStyle(
                                    alignment = LineHeightStyle.Alignment.Center,
                                    trim = LineHeightStyle.Trim.Both,
                                )
                            ),
                        ),
                    )
                    Text(
                        "${priceFormatter.format(subTotal)} 円",
                        color = Secondary,
                        fontSize = 14.sp,
                        style = LocalTextStyle.current.merge(
                            TextStyle(
                                fontWeight = FontWeight.Normal,
                                lineHeightStyle = LineHeightStyle(
                                    alignment = LineHeightStyle.Alignment.Center,
                                    trim = LineHeightStyle.Trim.Both,
                                )
                            ),
                        ),
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .padding(start = 14.dp, end = 14.dp, top = 12.dp)
                        .fillMaxWidth(),
                ) {
                    Text(
                        "Sum discount",
                        color = Gray500,
                        fontSize = 14.sp,
                        style = LocalTextStyle.current.merge(
                            TextStyle(
                                fontWeight = FontWeight.Normal,
                                lineHeightStyle = LineHeightStyle(
                                    alignment = LineHeightStyle.Alignment.Center,
                                    trim = LineHeightStyle.Trim.Both,
                                )
                            ),
                        ),
                    )
                    Text(
                        "${priceFormatter.format(sumDiscount)} 円",
                        color = Secondary,
                        fontSize = 14.sp,
                        style = LocalTextStyle.current.merge(
                            TextStyle(
                                fontWeight = FontWeight.Normal,
                                lineHeightStyle = LineHeightStyle(
                                    alignment = LineHeightStyle.Alignment.Center,
                                    trim = LineHeightStyle.Trim.Both,
                                )
                            ),
                        ),
                    )
                }

                Spacer(Modifier.height(12.dp))

                HorizontalDivider(
                    Modifier
                        .height(2.dp)
                        .padding(horizontal = 12.dp),
                    1.dp,
                    Gray100.copy(alpha = 0.2f)
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .padding(horizontal = 14.dp)
                        .fillMaxWidth(),
                ) {
                    Text(
                        "Total",
                        color = Secondary,
                        fontSize = 16.sp,
                        style = LocalTextStyle.current.merge(
                            TextStyle(
                                fontWeight = FontWeight.Normal,
                                lineHeightStyle = LineHeightStyle(
                                    alignment = LineHeightStyle.Alignment.Center,
                                    trim = LineHeightStyle.Trim.Both,
                                )
                            ),
                        ),
                    )
                    Text(
                        "${priceFormatter.format(total)} 円",
                        color = Primary,
                        fontSize = 20.sp,
                        style = LocalTextStyle.current.merge(
                            TextStyle(
                                fontWeight = FontWeight.Bold,
                                lineHeightStyle = LineHeightStyle(
                                    alignment = LineHeightStyle.Alignment.Center,
                                    trim = LineHeightStyle.Trim.Both,
                                )
                            ),
                        ),
                    )
                }

                // Spacer(Modifier.height(50.dp))
                Text(
                    "Payment Method",
                    modifier = Modifier
                        .padding(start = 14.dp, end = 14.dp, top = 14.dp)
                        .fillMaxWidth(),
                    color = Secondary,
                    fontSize = 14.sp,
                    style = LocalTextStyle.current.merge(
                        TextStyle(
                            fontWeight = FontWeight.Normal,
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.Both,
                            )
                        ),
                    ),
                )

                Spacer(Modifier.height(8.dp))
                LazyVerticalGrid(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    item {
                        PaymentMethodButton(
                            "Cash",
                            active = selectedPaymentMethod == PaymentMethod.CASH,
                            onClick = {
                                viewModel.onEvent(CashierEvent.OnSelectPaymentMethod(PaymentMethod.CASH))
                            }) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalPlatformContext.current)
                                    .data(R.raw.cash_payment_method_icon)
                                    .decoderFactory(SvgDecoder.Factory())
                                    .build(),
                                contentDescription = "Cash payment",
                                colorFilter = ColorFilter.tint(if (selectedPaymentMethod == PaymentMethod.CASH) Primary else White),
                                modifier = Modifier.size(15.dp)

                            )
                        }
                    }
                    item {
                        PaymentMethodButton(
                            "Card",
                            active = selectedPaymentMethod == PaymentMethod.CREDIT_CARD,
                            onClick = {
                                viewModel.onEvent(CashierEvent.OnSelectPaymentMethod(PaymentMethod.CREDIT_CARD))
                            }) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalPlatformContext.current)
                                    .data(R.raw.credit_card_payment_method_icon)
                                    .decoderFactory(SvgDecoder.Factory())
                                    .build(),
                                contentDescription = "Credit card payment",
                                colorFilter = ColorFilter.tint(if (selectedPaymentMethod == PaymentMethod.CREDIT_CARD) Primary else Secondary),
                                modifier = Modifier.size(15.dp)

                            )
                        }
                    }
                    item {
                        PaymentMethodButton(
                            "QR",
                            active = selectedPaymentMethod == PaymentMethod.QR,
                            onClick = {
                                viewModel.onEvent(CashierEvent.OnSelectPaymentMethod(PaymentMethod.QR))
                            }) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalPlatformContext.current)
                                    .data(R.raw.qr_code_payment_method)
                                    .decoderFactory(SvgDecoder.Factory())
                                    .build(),
                                contentDescription = "QR code payment",
                                colorFilter = ColorFilter.tint(if (selectedPaymentMethod == PaymentMethod.QR) Primary else Secondary),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }

                when (selectedPaymentMethod) {
                    PaymentMethod.CASH -> {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(horizontal = 14.dp)
                                .fillMaxWidth(),
                        ) {
                            Text(
                                "Amount received",
                                color = Gray500,
                                fontSize = 14.sp,
                                style = LocalTextStyle.current.merge(
                                    TextStyle(
                                        fontWeight = FontWeight.Normal,
                                        lineHeightStyle = LineHeightStyle(
                                            alignment = LineHeightStyle.Alignment.Center,
                                            trim = LineHeightStyle.Trim.Both,
                                        )
                                    ),
                                ),
                            )
                            TextField(
                                value = inpCashPaymentMethod.text,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                isError = inpCashPaymentMethod.isError,
                                //enabled = !transactionState.isLoading,
                                enabled = !transactionState.isLoading,
                                shape = RoundedCornerShape(0.dp),
                                onValueChange = { viewModel.onEvent(CashierEvent.EnteredCashBalance(it)) },
                                placeholder = {
                                    Text(
                                        "-",
                                        color = Gray200,
                                        textAlign = TextAlign.Right,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                },
                                singleLine = true,
                                visualTransformation = ThousandsSeparatorTransformation(),
                                textStyle = LocalTextStyle.current.copy(
                                    textAlign = TextAlign.End
                                ),
                                suffix = { Text("円", color = Secondary) },
                                colors = TextFieldDefaults.colors(
                                    cursorColor = Primary,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedIndicatorColor = Primary,
                                    errorIndicatorColor = Danger900,
                                    errorCursorColor = Danger900,
                                    errorContainerColor = Color.Transparent,
                                    disabledContainerColor = Primary100,
                                    disabledIndicatorColor = Primary500,
                                    focusedTextColor = Secondary,
                                    unfocusedTextColor = Secondary,
                                    disabledTextColor = Gray300,
                                    errorTextColor = Danger900,
                                ),
                                modifier = Modifier
                                    .width(180.dp)
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier
                                .padding(horizontal = 14.dp)
                                .fillMaxWidth(),
                        ) {
                            Text(
                                "Change",
                                color = Gray500,
                                fontSize = 14.sp,
                                style = LocalTextStyle.current.merge(
                                    TextStyle(
                                        fontWeight = FontWeight.Normal,
                                        lineHeightStyle = LineHeightStyle(
                                            alignment = LineHeightStyle.Alignment.Center,
                                            trim = LineHeightStyle.Trim.Both,
                                        )
                                    ),
                                ),
                            )
                            Text(
                                "${priceFormatter.format(if (inpCashPaymentMethod.text.isNotEmpty()) inpCashPaymentMethod.text.toInt() - subTotal else 0)} 円",
                                color = Secondary,
                                fontSize = 14.sp,
                                style = LocalTextStyle.current.merge(
                                    TextStyle(
                                        fontWeight = FontWeight.Normal,
                                        lineHeightStyle = LineHeightStyle(
                                            alignment = LineHeightStyle.Alignment.Center,
                                            trim = LineHeightStyle.Trim.Both,
                                        )
                                    ),
                                ),
                            )
                        }
                    }

                    PaymentMethod.CREDIT_CARD -> TODO()
                    PaymentMethod.QR -> TODO()
                }

                Spacer(Modifier.height(18.dp))
                Button(
                    enabled = !transactionState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = White,
                        disabledContentColor = Gray300,
                        disabledContainerColor = Gray100
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    onClick = { viewModel.onEvent(CashierEvent.PlaceOrder) },
                ) {
                    Text(
                        if (transactionState.isLoading) "Please wait..." else "Place Order",
                        color = White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}
