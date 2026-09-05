package com.pos.cashiersp.presentation.item_sales_log.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pos.cashiersp.presentation.item_sales_log.SalesLogScope
import com.pos.cashiersp.presentation.ui.theme.Gray100
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.White

/**
 * The two entry buttons: "All Items" vs "This Item".
 * Shown as a segmented control; before a choice is made neither side is highlighted.
 */
@Composable
fun ScopeSelector(
    selectedScope: SalesLogScope?,
    onSelect: (SalesLogScope) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(White)
            .border(1.dp, Gray100, RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ScopeButton(
            modifier = Modifier.weight(1f),
            label = "Search Item",
            isSelected = selectedScope == SalesLogScope.SINGLE_ITEM,
            onClick = { onSelect(SalesLogScope.SINGLE_ITEM) }
        )
        ScopeButton(
            modifier = Modifier.weight(1f),
            label = "All Items",
            isSelected = selectedScope == SalesLogScope.ALL_ITEMS,
            onClick = { onSelect(SalesLogScope.ALL_ITEMS) }
        )
    }
}

@Composable
private fun ScopeButton(
    modifier: Modifier = Modifier,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(9.dp))
            .background(if (isSelected) Primary else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) White else Secondary
            )
        )
    }
}

