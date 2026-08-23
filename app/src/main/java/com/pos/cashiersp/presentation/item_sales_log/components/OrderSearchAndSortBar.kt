package com.pos.cashiersp.presentation.item_sales_log.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pos.cashiersp.presentation.ui.theme.Gray100
import com.pos.cashiersp.presentation.ui.theme.Gray400
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Purple300
import com.pos.cashiersp.presentation.ui.theme.White

@Composable
fun OrderSearchAndSortBar(
    sortLabel: String,
    onSortClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .height(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(White)
                .border(1.dp, Gray100, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = Gray400,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Search order ID...",
                style = TextStyle(fontSize = 14.sp, color = Gray400)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Sort/filter button — opens FilterBottomSheet, label reflects the applied sort.
        Row(
            modifier = Modifier
                .height(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(White)
                .border(1.dp, Purple300, RoundedCornerShape(12.dp))
                .clickable(onClick = onSortClick)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "↑↓",
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Primary)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = sortLabel,
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Primary)
            )
        }
    }
}