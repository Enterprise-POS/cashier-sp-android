package com.pos.cashiersp.presentation.cashier.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
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
import com.pos.cashiersp.model.domain.CartItem
import com.pos.cashiersp.presentation.cashier.CashierEvent
import com.pos.cashiersp.presentation.cashier.CashierViewModel
import com.pos.cashiersp.presentation.ui.theme.Danger900
import com.pos.cashiersp.presentation.ui.theme.Gray100
import com.pos.cashiersp.presentation.ui.theme.Gray200
import com.pos.cashiersp.presentation.ui.theme.Gray300
import com.pos.cashiersp.presentation.ui.theme.Gray500
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Primary100
import com.pos.cashiersp.presentation.ui.theme.Primary500
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.White
import com.pos.cashiersp.presentation.util.PaymentMethod
import com.pos.cashiersp.presentation.util.ThousandsSeparatorTransformation
import com.pos.cashiersp.presentation.util.toRupiah
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun PaymentSummary(
    modifier: Modifier,
    viewModel: CashierViewModel = hiltViewModel()
) {
    val cart: Map<Int, CartItem> = viewModel.cart.value
    val selectedPaymentMethod = viewModel.selectedPaymentMethod.value
    val inpCashPaymentMethod = viewModel.inpCashPaymentMethod.value
    val transactionState = viewModel.transactionState.value


    // Feature for sumDiscount (not implemented)
    val sumDiscount: Float = 0f

    val subTotal = remember(cart) {
        cart.entries.fold(0) { acc, (_, cartItem) ->
            acc + (cartItem.storeStock.price * cartItem.quantity)
        }
    }

    val total = subTotal + sumDiscount

    Column(
        modifier = modifier
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
                style = TextStyle(
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.Both,
                    )
                ),
            )
            Text(
                subTotal.toRupiah(),
                color = Secondary,
                fontSize = 14.sp,
                style = TextStyle(
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.Both,
                    )
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
                style = TextStyle(
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.Both,
                    )
                ),
            )
            Text(
                sumDiscount.toRupiah(),
                color = Secondary,
                fontSize = 14.sp,
                style = TextStyle(
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.Both,
                    )
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
                style = TextStyle(
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.Both,
                    )
                ),
            )
            Text(
                total.toRupiah(),
                color = Primary,
                fontSize = 20.sp,
                style = TextStyle(
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.Both,
                    )
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
            style = TextStyle(
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.Both,
                )
            ),
        )

        Spacer(Modifier.height(8.dp))
        LazyHorizontalStaggeredGrid(
            rows = StaggeredGridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalItemSpacing = 8.dp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(32.dp)
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
                        colorFilter = ColorFilter.tint(if (selectedPaymentMethod == PaymentMethod.CASH) Primary else Secondary),
                        modifier = Modifier.size(15.dp)

                    )
                }
            }
            item {
                PaymentMethodButton(
                    "Card",
                    active = selectedPaymentMethod == PaymentMethod.CARD,
                    onClick = {
                        viewModel.onEvent(CashierEvent.OnSelectPaymentMethod(PaymentMethod.CARD))
                    }) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalPlatformContext.current)
                            .data(R.raw.credit_card_payment_method_icon)
                            .decoderFactory(SvgDecoder.Factory())
                            .build(),
                        contentDescription = "Credit card payment",
                        colorFilter = ColorFilter.tint(if (selectedPaymentMethod == PaymentMethod.CARD) Primary else Secondary),
                        modifier = Modifier.size(15.dp)

                    )
                }
            }
            item {
                PaymentMethodButton(
                    "QRIS",
                    active = selectedPaymentMethod == PaymentMethod.QRIS,
                    onClick = {
                        viewModel.onEvent(CashierEvent.OnSelectPaymentMethod(PaymentMethod.QRIS))
                    }) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalPlatformContext.current)
                            .data(R.raw.qr_code_payment_method)
                            .decoderFactory(SvgDecoder.Factory())
                            .build(),
                        contentDescription = "QR code payment",
                        colorFilter = ColorFilter.tint(if (selectedPaymentMethod == PaymentMethod.QRIS) Primary else Secondary),
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
            item {
                PaymentMethodButton(
                    "Other",
                    active = selectedPaymentMethod == PaymentMethod.OTHER,
                    onClick = {
                        viewModel.onEvent(CashierEvent.OnSelectPaymentMethod(PaymentMethod.OTHER))
                    }) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Other payment method",
                        tint = if (selectedPaymentMethod == PaymentMethod.OTHER) Primary else Secondary,
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
                        style = TextStyle(
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.Both,
                            )
                        ),
                    )
                    TextField(
                        value = inpCashPaymentMethod.text,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = inpCashPaymentMethod.isError,
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
                        prefix = { Text("Rp", color = Secondary) },
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
                        style = TextStyle(
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.Both,
                            )
                        ),
                    )
                    Text(
                        if (inpCashPaymentMethod.text.isNotEmpty()) (inpCashPaymentMethod.text.toInt() - subTotal).toRupiah() else "Rp 0",
                        color = Secondary,
                        fontSize = 14.sp,
                        style = TextStyle(
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.Both,
                            )
                        ),
                    )
                }
            }

            PaymentMethod.CARD -> TODO()
            PaymentMethod.QRIS -> TODO()
            PaymentMethod.EWALLET -> TODO()
            PaymentMethod.OTHER -> {
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
                        style = TextStyle(
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.Both,
                            )
                        ),
                    )
                    TextField(
                        value = total.toString(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = inpCashPaymentMethod.isError,
                        enabled = false,
                        shape = RoundedCornerShape(0.dp),
                        onValueChange = { },
                        placeholder = {
                            Text(
                                "-",
                                color = Gray200,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        singleLine = true,
                        visualTransformation = VisualTransformation.None,
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.End
                        ),
                        prefix = { Text("Rp", color = Secondary) },
                        colors = TextFieldDefaults.colors(
                            cursorColor = Primary,
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Primary,
                            errorIndicatorColor = Danger900,
                            errorCursorColor = Danger900,
                            errorContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
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
                        "Make sure the all value correct from before place order",
                        color = Gray500,
                        fontSize = 14.sp,
                        style = TextStyle(
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.Both,
                            )
                        ),
                    )
                }
            }
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
        Spacer(Modifier.height(8.dp))
    }
}