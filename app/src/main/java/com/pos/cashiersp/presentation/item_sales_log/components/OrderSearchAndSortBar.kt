package com.pos.cashiersp.presentation.item_sales_log.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.pos.cashiersp.presentation.item_sales_log.ItemSalesLogEvent
import com.pos.cashiersp.presentation.item_sales_log.ItemSalesLogViewModel
import com.pos.cashiersp.presentation.item_sales_log.SalesLogScope
import com.pos.cashiersp.presentation.item_sales_log.SortColumn
import com.pos.cashiersp.presentation.ui.theme.Gray100
import com.pos.cashiersp.presentation.ui.theme.Gray400
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Purple300
import com.pos.cashiersp.presentation.ui.theme.White

private fun sortButtonLabel(
    column: SortColumn,
    ascending: Boolean
): String {
    return when {
        column == SortColumn.DATE && !ascending -> "Latest"
        column == SortColumn.DATE && ascending -> "Oldest"
        else -> "${column.displayName} ${if (ascending) "↑" else "↓"}"
    }
}

val orders = listOf(
    OrderSuggestion("1227", "Apple Candy", 90),
    OrderSuggestion("5279", "Demo Migrate 1", 0),
    OrderSuggestion("1417", "Iceland Ice Cream", 20_000),
    OrderSuggestion("8811", "Mojitos", 88),
    OrderSuggestion("3675", "Muffins", 250),
    OrderSuggestion("1226", "Pineapple Pie XO", 120),
    OrderSuggestion("1228", "Pizza's Tar", 0),
    OrderSuggestion("5922", "Re Ice Cream", 1_500),
    OrderSuggestion("4364", "Test Item 2", 0)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSearchAndSortBar(
    onSortClick: () -> Unit,
    sortColumn: SortColumn,
    sortAscending: Boolean,
    selectedScope: SalesLogScope?,
    viewModel: ItemSalesLogViewModel = hiltViewModel()
) {
    var searchItemId = viewModel.searchSortBarInp.value
    var expanded by remember { mutableStateOf(false) }

    // Tracks the measured width of the text field so the dropdown
    // (plain DropdownMenu doesn't auto-match anchor width like
    // ExposedDropdownMenu does) can be sized to match it.
    var fieldWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    val focusRequester = remember { FocusRequester() }

    val suggestions = remember(searchItemId) {
        if (searchItemId.isBlank()) {
            orders
        } else {
            orders.filter { order ->
                order.id.contains(searchItemId, ignoreCase = true) ||
                        order.name.contains(searchItemId, ignoreCase = true)
            }
        }
    }

    // Re-focus the field whenever the dropdown opens, in case
    // anything else has stolen focus in the meantime.
    LaunchedEffect(expanded) {
        if (expanded) focusRequester.requestFocus()
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {

        // ORDER SEARCH / DROPDOWN
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.weight(1f)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .onGloballyPositioned { coordinates ->
                        fieldWidth = with(density) { coordinates.size.width.toDp() }
                    }
                    .clip(
                        RoundedCornerShape(12.dp)
                    )
                    .background(White)
                    .border(
                        width = 1.dp,
                        color = Gray100,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = Gray400,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                BasicTextField(
                    value = if (selectedScope == SalesLogScope.ALL_ITEMS) "" else searchItemId,
                    enabled = selectedScope == SalesLogScope.SINGLE_ITEM,
                    onValueChange = {
                        viewModel.onEvent(ItemSalesLogEvent.OnChangeSearchItemId(it))
                        // Typing should keep/reopen the dropdown, never lock it.
                        expanded = true
                    },
                    // Always editable — no readOnly toggling
                    readOnly = false,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = if (selectedScope == SalesLogScope.SINGLE_ITEM) Color.Black else Color.Gray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .focusRequester(focusRequester),
                    decorationBox = { innerTextField ->

                        if (searchItemId.isEmpty()) {
                            Text(
                                text = "Search order ID...",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = Gray400
                                )
                            )
                        }

                        innerTextField()
                    }
                )
            }

            // DROPDOWN
            //
            // Using plain DropdownMenu (not ExposedDropdownMenu) so we
            // can set properties = PopupProperties(focusable = false).
            // A focusable popup recomposing on every keystroke was
            // stealing focus from the text field mid-typing.
            DropdownMenu(
                expanded = if (selectedScope == SalesLogScope.SINGLE_ITEM) expanded else false,
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = false),
                modifier = Modifier
                    .background(White)
                    .width(fieldWidth)
                    .heightIn(max = 500.dp)
            ) {

                suggestions.forEach { order ->

                    DropdownMenuItem(
                        text = {
                            Column {

                                Text(
                                    text = order.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                Text(
                                    text = "ID · ${order.id}",
                                    fontSize = 12.sp,
                                    color = Gray400
                                )
                            }
                        },

                        onClick = {
                            searchItemId = order.id
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        // SORT BUTTON
        Row(
            modifier = Modifier
                .height(46.dp)
                .clip(
                    RoundedCornerShape(12.dp)
                )
                .background(White)
                .border(
                    width = 1.dp,
                    color = Purple300,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable(
                    onClick = onSortClick
                )
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "↑↓",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            )

            Spacer(
                modifier = Modifier.width(6.dp)
            )

            Text(
                text = sortButtonLabel(
                    sortColumn,
                    sortAscending
                ),
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Primary
                )
            )
        }
    }
}

data class OrderSuggestion(
    val id: String,
    val name: String,
    val price: Int
)