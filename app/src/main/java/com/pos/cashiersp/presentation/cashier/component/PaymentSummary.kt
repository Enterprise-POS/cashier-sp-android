package com.pos.cashiersp.presentation.cashier.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.outlined.MoneyOff
import androidx.compose.material.icons.outlined.Percent
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import com.pos.cashiersp.model.domain.Voucher
import com.pos.cashiersp.model.dto.VoucherType
import com.pos.cashiersp.presentation.cashier.CashierEvent
import com.pos.cashiersp.presentation.cashier.CashierViewModel
import com.pos.cashiersp.presentation.ui.theme.Danger900
import com.pos.cashiersp.presentation.ui.theme.Gray100
import com.pos.cashiersp.presentation.ui.theme.Gray200
import com.pos.cashiersp.presentation.ui.theme.Gray300
import com.pos.cashiersp.presentation.ui.theme.Gray500
import com.pos.cashiersp.presentation.ui.theme.Light300
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Primary100
import com.pos.cashiersp.presentation.ui.theme.Primary500
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.Secondary100
import com.pos.cashiersp.presentation.ui.theme.White
import com.pos.cashiersp.presentation.util.KeypadNumberElement
import com.pos.cashiersp.presentation.util.PaymentMethod
import com.pos.cashiersp.presentation.util.QuickSelectAmount
import com.pos.cashiersp.presentation.util.ThousandsSeparatorTransformation
import com.pos.cashiersp.presentation.util.toRupiah

@Composable
fun PaymentSummary(
    modifier: Modifier,
    viewModel: CashierViewModel = hiltViewModel(),
    showCashKeypad: Boolean = false,
    onCancel: (() -> Unit)? = null,
) {
    val cart: Map<Int, CartItem> = viewModel.cart.value
    val selectedPaymentMethod = viewModel.selectedPaymentMethod.value
    val selectedQuickSelectAmount = viewModel.selectedQuickAmount.value
    val inpCashPaymentMethod = viewModel.inpCashPaymentMethod.value
    val transactionState = viewModel.transactionState.value
    val voucherCode = viewModel.voucherCode.value
    val appliedVoucherCodes = viewModel.appliedVoucherCodes.value
    val isVoucherPickerOpen = viewModel.isVoucherPickerOpen.value


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
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
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

            VoucherSection(
                voucherCode = voucherCode,
                onVoucherCodeChange = { viewModel.onEvent(CashierEvent.OnVoucherCodeChange(it)) },
                appliedVoucherCodes = appliedVoucherCodes,
                onAddVoucher = { viewModel.onEvent(CashierEvent.OnAddVoucher) },
                onSelectVoucher = { viewModel.onEvent(CashierEvent.OnOpenVoucherPicker) },
                onRemoveVoucher = { viewModel.onEvent(CashierEvent.OnRemoveAppliedVoucher(it)) },
            )

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
                            colorFilter = ColorFilter.tint(if (selectedPaymentMethod == PaymentMethod.CARD) Primary else Secondary),
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
                    Text(
                        "Quick select amount",
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
                    if (showCashKeypad) {
                        CashNumberPad { viewModel.onEvent(CashierEvent.OnKeypadClicked(it)) }
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
                PaymentMethod.EWALLET -> TODO()
                PaymentMethod.QRIS, PaymentMethod.OTHER -> {
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

        }

        Spacer(Modifier.height(18.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            if (onCancel != null) {
                OutlinedButton(
                    onClick = onCancel,
                    enabled = !transactionState.isLoading,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary),
                ) {
                    Text("Cancel", fontSize = 14.sp)
                }
            }
            Button(
                enabled = !transactionState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor = White,
                    disabledContentColor = Gray300,
                    disabledContainerColor = Gray100
                ),
                modifier = Modifier.weight(if (onCancel == null) 1f else 2f),
                onClick = { viewModel.onEvent(CashierEvent.PlaceOrder) },
            ) {
                Text(
                    if (transactionState.isLoading) "Please wait..." else "Place Order",
                    color = Light300,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
    }

    if (isVoucherPickerOpen) {
        VoucherPickerDialog(viewModel)
    }
}

@Composable
private fun VoucherSection(
    voucherCode: String,
    onVoucherCodeChange: (String) -> Unit,
    appliedVoucherCodes: List<String>,
    onAddVoucher: () -> Unit,
    onSelectVoucher: () -> Unit,
    onRemoveVoucher: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
    ) {
        Text(
            "Voucher",
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            color = Secondary,
            fontSize = 14.sp,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            TextField(
                value = voucherCode,
                onValueChange = onVoucherCodeChange,
                placeholder = {
                    Text(
                        "input voucher code...",
                        color = Gray200,
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(0.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Secondary,
                    unfocusedTextColor = Secondary,
                    cursorColor = Primary,
                ),
                modifier = Modifier.weight(1f),
            )
            Surface(
                onClick = onSelectVoucher,
                modifier = Modifier.size(48.dp),
                color = White,
                shape = RoundedCornerShape(50),
                border = BorderStroke(.4.dp, Gray100),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.ConfirmationNumber,
                        contentDescription = "Select voucher",
                        tint = Secondary,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Button(
                onClick = onAddVoucher,
                enabled = voucherCode.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor = White,
                    disabledContentColor = Gray300,
                    disabledContainerColor = Gray100,
                ),
                contentPadding = PaddingValues(horizontal = 10.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add voucher",
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(4.dp))
                Text("Add")
            }
        }

        if (appliedVoucherCodes.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .horizontalScroll(rememberScrollState()),
            ) {
                appliedVoucherCodes.forEachIndexed { index, code ->
                    AppliedVoucherChip(
                        code = code,
                        onRemove = { onRemoveVoucher(index) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AppliedVoucherChip(code: String, onRemove: () -> Unit) {
    PaymentMethodButton(
        paymentMethodName = code,
        active = true,
        onClick = {},
        modifier = Modifier.wrapContentWidth(),
        trailingAction = {
            Surface(
                onClick = onRemove,
                modifier = Modifier.size(28.dp),
                shape = CircleShape,
                color = Color.Transparent,
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove voucher $code",
                        tint = Primary,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        },
        icon = {},
    )
}

@Composable
private fun VoucherPickerDialog(viewModel: CashierViewModel) {
    val categories = listOf(
        null to "All vouchers",
        VoucherType.FROM_TOTAL_LIMITED_PERCENTAGE to "Percentage",
        VoucherType.FROM_TOTAL_LIMITED_STATIC_DECREMENT to "Decrement",
        VoucherType.ITEM_COMBINATIONS to "Item combination",
    )
    val selectedCategory = viewModel.selectedVoucherCategory.value
    val vouchers = viewModel.vouchers.value.filter {
        it.voucherType != VoucherType.ITEM_QUANTITIES &&
            (selectedCategory == null || it.voucherType == selectedCategory)
    }

    Dialog(
        onDismissRequest = { viewModel.onEvent(CashierEvent.OnDismissVoucherPicker) },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(.94f)
                .widthIn(max = 640.dp)
                .fillMaxHeight(.88f)
                .heightIn(max = 760.dp),
            shape = RoundedCornerShape(24.dp),
            color = Light300,
            shadowElevation = 12.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Select a voucher",
                            color = Secondary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            "Choose a voucher to add to this transaction",
                            color = Gray500,
                            fontSize = 13.sp,
                        )
                    }
                    IconButton(
                        onClick = { viewModel.onEvent(CashierEvent.OnDismissVoucherPicker) },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close voucher picker",
                            tint = Secondary,
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                Column {
                    categories.chunked(2).forEachIndexed { rowIndex, rowCategories ->
                        Row(Modifier.fillMaxWidth()) {
                            rowCategories.forEachIndexed { index, (type, label) ->
                                val selected = selectedCategory == type
                                Surface(
                                    onClick = {
                                        viewModel.onEvent(CashierEvent.OnSelectVoucherCategory(type))
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 44.dp),
                                    shape = RoundedCornerShape(
                                        topStart = if (rowIndex == 0 && index == 0) 12.dp else 0.dp,
                                        topEnd = if (rowIndex == 0 && index == 1) 12.dp else 0.dp,
                                        bottomEnd = if (rowIndex == 1 && index == 1) 12.dp else 0.dp,
                                        bottomStart = if (rowIndex == 1 && index == 0) 12.dp else 0.dp,
                                    ),
                                    color = if (selected) Primary else White,
                                    border = BorderStroke(
                                        1.dp,
                                        if (selected) Secondary else Gray100,
                                    ),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp, vertical = 6.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = label,
                                            color = Secondary,
                                            fontSize = 13.sp,
                                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                            maxLines = 3,
                                            textAlign = TextAlign.Center,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))
                Text(
                    text = "${vouchers.size} available",
                    color = Gray500,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.height(4.dp))

                if (vouchers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.ConfirmationNumber,
                                contentDescription = null,
                                tint = Gray300,
                                modifier = Modifier.size(40.dp),
                            )
                            Spacer(Modifier.height(10.dp))
                            Text("No vouchers in this category", color = Gray500)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(vouchers, key = { it.id }) { voucher ->
                            VoucherPickerCard(voucher) {
                                viewModel.onEvent(CashierEvent.OnSelectVoucher(voucher))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VoucherPickerCard(voucher: Voucher, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = White,
        border = BorderStroke(1.dp, Gray100.copy(alpha = .7f)),
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(12.dp),
                color = Primary100,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = voucher.voucherType.toVoucherTypeIcon(),
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
            Spacer(Modifier.width(10.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = voucher.name,
                    color = Secondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Secondary100,
                    ) {
                        Text(
                            text = voucher.code,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            color = Secondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Primary100,
                    ) {
                        Text(
                            text = voucherOfferSummary(voucher),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            color = Secondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Text(
                    text = voucher.voucherType.toDisplayName(),
                    color = Gray500,
                    fontSize = 11.sp,
                )
            }
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Select ${voucher.code}",
                tint = Secondary,
            )
        }
    }
}

private fun voucherOfferSummary(voucher: Voucher): String =
    when (voucher.voucherType) {
        VoucherType.FROM_TOTAL_LIMITED_PERCENTAGE -> "${voucher.percentage}% off"
        VoucherType.FROM_TOTAL_LIMITED_STATIC_DECREMENT,
        VoucherType.ITEM_COMBINATIONS -> "${voucher.decrement.toRupiah()} off"
        VoucherType.ITEM_QUANTITIES -> "Discount"
    }

private fun VoucherType.toDisplayName(): String = when (this) {
    VoucherType.FROM_TOTAL_LIMITED_PERCENTAGE -> "Percentage"
    VoucherType.FROM_TOTAL_LIMITED_STATIC_DECREMENT -> "Decrement"
    VoucherType.ITEM_COMBINATIONS -> "Item combination"
    VoucherType.ITEM_QUANTITIES -> "Item quantities"
}

private fun VoucherType.toVoucherTypeIcon(): ImageVector = when (this) {
    VoucherType.FROM_TOTAL_LIMITED_PERCENTAGE -> Icons.Outlined.Percent
    VoucherType.FROM_TOTAL_LIMITED_STATIC_DECREMENT -> Icons.Outlined.MoneyOff
    VoucherType.ITEM_COMBINATIONS -> Icons.Outlined.ShoppingBasket
    VoucherType.ITEM_QUANTITIES -> Icons.Default.ConfirmationNumber
}

@Composable
private fun CashNumberPad(onClick: (key: KeypadNumberElement) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text("Cash keypad", color = Secondary, fontSize = 13.sp)
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
                                .height(38.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = when {
                                isPrimaryKey -> Primary
                                isActionKey -> Primary100
                                else -> White
                            },
                            border = BorderStroke(
                                1.dp,
                                if (isPrimaryKey) Primary else Primary.copy(alpha = .25f)
                            )
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = key.uiLabel,
                                    color = if (isPrimaryKey) White else Primary,
                                    fontSize = if (isActionKey) 12.sp else 16.sp
                                )
                            }
                        }
                    }
                }
            }
    }
}