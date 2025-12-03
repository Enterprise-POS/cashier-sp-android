package com.pos.cashiersp.presentation.greeting.component

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pos.cashiersp.common.TestTags
import com.pos.cashiersp.presentation.ui.theme.Gray300
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.Secondary100
import com.pos.cashiersp.presentation.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashierPartialBottomSheet(
    showBottomSheet: Boolean,
    sheetState: SheetState,
    itemChart: Map<String, Int>,
    itemListRef: List<String>,
    onDismissRequest: () -> Unit
) {
    //    var text by remember { mutableStateOf("") }
    //    var isError by remember { mutableStateOf(false) }
    //    val maxWords = 50

    val priceFormatter = java.text.DecimalFormat("#,###")

    val customerName = "Aaron Fabian"

    var subTotal = 0
    for ((itemId, quantity) in itemChart) {
        val itemRef: String? = "test value"
        if (itemRef != null) {
            subTotal += 10_000 * quantity
        }
    }

    val priceTax: Float = subTotal / 10f
    val total = subTotal + priceTax

    if (showBottomSheet) {
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
                    Text(
                        "Order no. 77",
                        color = Secondary,
                        fontSize = 18.sp,
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
                    Spacer(Modifier.height(6.dp))
                    Text(
                        customerName,
                        color = Secondary,
                        fontSize = 12.sp,
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
                        itemChart.entries.toList().forEachIndexed { index: Int, (itemId: String, quantity: Int) ->
                            val itemRef: String? = "item value"
                            if (itemRef != null) {
                                item { FoodItem(index + 1, itemRef, quantity) }
                            }
                        }
                    }

                    // This spacer makes room for bottom content
                    Spacer(modifier = Modifier.height(360.dp))
                }

                // Bottom column with fixed height
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(360.dp) // This must be the same value as the padding at Column
                        .padding(8.dp)
                        .background(
                            color = Secondary,
                            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                        )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                            .fillMaxWidth(),
                    ) {
                        Text(
                            "Subtotal",
                            color = White,
                            fontSize = 12.sp,
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
                            "Rp ${priceFormatter.format(subTotal)}",
                            color = White,
                            fontSize = 12.sp,
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
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                            .fillMaxWidth(),
                    ) {
                        Text(
                            "Tax",
                            color = White,
                            fontSize = 12.sp,
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
                            "Rp ${priceFormatter.format(priceTax)}",
                            color = White,
                            fontSize = 12.sp,
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
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                            .fillMaxWidth(),
                    ) {
                        Text(
                            "Total",
                            color = White,
                            fontSize = 20.sp,
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
                            "Rp ${priceFormatter.format(total)}",
                            color = White,
                            fontSize = 20.sp,
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

                    Spacer(Modifier.height(50.dp))
                    Text(
                        "Payment Method",
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                            .fillMaxWidth(),
                        color = White,
                        fontSize = 12.sp,
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
                        modifier = Modifier.padding(horizontal = 18.dp)
                    ) {
                        items(3) { _: Int ->
                            Column {
                                IconButton(
                                    enabled = true,
                                    modifier = Modifier
                                        .fillMaxSize() // This center the icon
                                        .border(
                                            width = 1.dp,
                                            color = Primary,
                                            shape = RoundedCornerShape(8.dp)
                                        ),
                                    onClick = {}
                                ) {
                                    Icon(
                                        Icons.Filled.AddCircle,
                                        tint = Primary,
                                        contentDescription = "Add ... payment method",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Cash",
                                    color = White,
                                    fontSize = 12.sp,
                                    modifier = Modifier.fillMaxWidth(),
                                    style = LocalTextStyle.current.merge(
                                        TextStyle(
                                            textAlign = TextAlign.Center,
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
                    }

                    Spacer(Modifier.height(18.dp))
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = Color.White,
                            disabledContainerColor = Color.Gray,
                            disabledContentColor = Color.LightGray
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        onClick = { }) {
                        Text(
                            "Place Order",
                            color = White,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun FoodItem(number: Int, item: String, quantity: Int) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Secondary), // dark background
        modifier = Modifier.fillMaxWidth(1f),
    ) {
        val priceFormatter = java.text.DecimalFormat("#,###")

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
                    "Here is Item Name",
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
                text = "Rp ${priceFormatter.format(10_000 * quantity)}",
                color = White,
                style = TextStyle(fontSize = 12.sp)
            )
        }
    }
}