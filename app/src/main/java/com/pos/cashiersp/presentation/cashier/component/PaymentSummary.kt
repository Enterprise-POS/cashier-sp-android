package com.pos.cashiersp.presentation.cashier.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import com.pos.cashiersp.R
import com.pos.cashiersp.model.domain.CartItem
import com.pos.cashiersp.presentation.cashier.CashierEvent
import com.pos.cashiersp.presentation.cashier.CashierViewModel
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.util.KeypadNumberElement
import com.pos.cashiersp.presentation.util.PaymentMethod
import com.pos.cashiersp.presentation.util.QuickSelectAmount
import com.pos.cashiersp.presentation.util.ThousandsSeparatorTransformation
import com.pos.cashiersp.presentation.util.toRupiah

internal data class PaymentTotals(
    val subTotal: Int,
    val sumDiscount: Float,
    val total: Float,
)

internal fun calculatePaymentTotals(cart: Map<Int, CartItem>): PaymentTotals {
    val subTotal = cart.entries.fold(0) { acc, (_, cartItem) ->
        acc + (cartItem.storeStock.price * cartItem.quantity)
    }

    // Feature for sumDiscount (not implemented)
    val sumDiscount = 0f
    return PaymentTotals(subTotal, sumDiscount, subTotal + sumDiscount)
}

@Composable
fun PaymentSummary(
    modifier: Modifier,
    viewModel: CashierViewModel = hiltViewModel(),
    showCashKeypad: Boolean = false
) {
    val cart: Map<Int, CartItem> = viewModel.cart.value
    val selectedPaymentMethod = viewModel.selectedPaymentMethod.value
    val selectedQuickSelectAmount = viewModel.selectedQuickAmount.value
    val inpCashPaymentMethod = viewModel.inpCashPaymentMethod.value
    val transactionState = viewModel.transactionState.value


    val paymentTotals = remember(cart) { calculatePaymentTotals(cart) }
    val subTotal = paymentTotals.subTotal
    val total = paymentTotals.total
    val sumDiscount = paymentTotals.sumDiscount

    Column(
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                .fillMaxWidth(),
        ) {
            Text(
                "Subtotal",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                subTotal.toRupiah(),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 12.dp)
                .fillMaxWidth(),
        ) {
            Text(
                "Sum discount",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                sumDiscount.toRupiah(),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Spacer(Modifier.height(12.dp))

        HorizontalDivider(
            Modifier
                .height(2.dp)
                .padding(horizontal = 16.dp),
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )

        Spacer(Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
        ) {
            Text(
                "Total",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                total.toRupiah(),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineSmall,
            )
        }

        // Spacer(Modifier.height(50.dp))
        Text(
            "Payment Method",
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleSmall,
        )

        Spacer(Modifier.height(8.dp))
        LazyHorizontalStaggeredGrid(
            rows = StaggeredGridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalItemSpacing = 8.dp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(48.dp)
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
                        colorFilter = ColorFilter.tint(
                            if (selectedPaymentMethod == PaymentMethod.CASH) Secondary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.size(15.dp)

                    )
                }
            }
            /*
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
                        colorFilter = ColorFilter.tint(
                            if (selectedPaymentMethod == PaymentMethod.CARD) Secondary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.size(15.dp)

                    )
                }
            }
             */
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
                        colorFilter = ColorFilter.tint(
                            if (selectedPaymentMethod == PaymentMethod.QRIS) Secondary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
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
                        tint = if (selectedPaymentMethod == PaymentMethod.OTHER) {
                            Secondary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }

        when (selectedPaymentMethod) {
            PaymentMethod.CASH -> {
                Text(
                    "Quick select amount",
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleSmall,
                )
                Spacer(Modifier.height(8.dp))
                LazyHorizontalStaggeredGrid(
                    rows = StaggeredGridCells.Fixed(1),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalItemSpacing = 8.dp,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    item {
                        PaymentMethodButton(
                            10000.toRupiah(),
                            active = false, // Make the color is inactive even it's clicked
                            onClick = {
                                viewModel.onEvent(CashierEvent.OnQuickSelectAmount(QuickSelectAmount.TEN_THOUNDSAND_RUPIAH))
                            }) {


                        }
                    }
                    item {
                        PaymentMethodButton(
                            20000.toRupiah(),
                            active = false, // Make the color is inactive even it's clicked
                            onClick = {
                                viewModel.onEvent(CashierEvent.OnQuickSelectAmount(QuickSelectAmount.TWENTY_THOUNDSAND_RUPIAH))
                            }) {


                        }
                    }
                    item {
                        PaymentMethodButton(
                            50000.toRupiah(),
                            active = false, // Make the color is inactive even it's clicked
                            onClick = {
                                viewModel.onEvent(CashierEvent.OnQuickSelectAmount(QuickSelectAmount.FIFTY_THOUNDSAND_RUPIAH))
                            }) {
                        }
                    }
                    item {
                        PaymentMethodButton(
                            100000.toRupiah(),
                            active = false, // Make the color is inactive even it's clicked
                            onClick = {
                                viewModel.onEvent(CashierEvent.OnQuickSelectAmount(QuickSelectAmount.HUNDRED_THOUNDSAND_RUPIAH))
                            }) {}
                    }
                    item {
                        PaymentMethodButton(
                            "Exact",
                            active = false, // Make the color is inactive even it's clicked
                            onClick = {
                                viewModel.onEvent(CashierEvent.OnQuickSelectAmount(QuickSelectAmount.EXACT))
                            }) {}
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                ) {
                    Text(
                        "Amount received",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
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
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        singleLine = true,
                        visualTransformation = ThousandsSeparatorTransformation(),
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.End
                        ),
                        prefix = { Text("Rp", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        colors = TextFieldDefaults.colors(
                            cursorColor = MaterialTheme.colorScheme.secondary,
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                            errorIndicatorColor = MaterialTheme.colorScheme.error,
                            errorCursorColor = MaterialTheme.colorScheme.error,
                            errorContainerColor = Color.Transparent,
                            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            disabledIndicatorColor = MaterialTheme.colorScheme.secondary,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            errorTextColor = MaterialTheme.colorScheme.error,
                        ),
                        modifier = Modifier
                            .width(180.dp)
                    )
                }
                if (showCashKeypad) {
                    CashNumberPad { viewModel.onEvent(CashierEvent.OnKeypadClicked(it)) }
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                ) {
                    Text(
                        "Change",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        if (inpCashPaymentMethod.text.isNotEmpty()) (inpCashPaymentMethod.text.toInt() - subTotal).toRupiah() else "Rp 0",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
            }

            PaymentMethod.CARD -> TODO()
            PaymentMethod.EWALLET -> TODO()
            PaymentMethod.QRIS, PaymentMethod.OTHER -> {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                ) {
                    Text(
                        "Amount received",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
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
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        singleLine = true,
                        visualTransformation = VisualTransformation.None,
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.End
                        ),
                        prefix = { Text("Rp", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        colors = TextFieldDefaults.colors(
                            cursorColor = MaterialTheme.colorScheme.secondary,
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                            errorIndicatorColor = MaterialTheme.colorScheme.error,
                            errorCursorColor = MaterialTheme.colorScheme.error,
                            errorContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            errorTextColor = MaterialTheme.colorScheme.error,
                        ),
                        modifier = Modifier
                            .width(180.dp)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                ) {
                    Text(
                        "Make sure the all value correct from before place order",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))
        Button(
            enabled = !transactionState.isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary,
                contentColor = Secondary,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            onClick = { viewModel.onEvent(CashierEvent.PlaceOrder) },
        ) {
            Text(
                if (transactionState.isLoading) "Please wait..." else "Place Order",
                color = Secondary,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun CashNumberPad(onClick: (key: KeypadNumberElement) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            "Cash keypad",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleSmall
        )
        KeypadNumberElement.entries.toList()
            .chunked(3)
            .forEach { rowKeys ->
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    rowKeys.forEach { key ->
                        val isPrimaryKey = key == KeypadNumberElement.ZERO
                        val isActionKey = key == KeypadNumberElement.BACKSPACE || key == KeypadNumberElement.CLEAR
                        Surface(
                            onClick = { onClick(key) },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = when {
                                isPrimaryKey -> Primary
                                isActionKey -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.surface
                            },
                            border = BorderStroke(
                                1.dp,
                                if (isPrimaryKey) Secondary
                                else MaterialTheme.colorScheme.outlineVariant
                            )
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = key.uiLabel,
                                    color = if (isPrimaryKey) {
                                        Secondary
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    },
                                    style = if (isActionKey) MaterialTheme.typography.labelLarge
                                    else MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            }
    }
}