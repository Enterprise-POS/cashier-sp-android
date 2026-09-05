package com.pos.cashiersp.presentation.invoice_detail.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pos.cashiersp.model.domain.OrderItem
import com.pos.cashiersp.presentation.invoice_detail.InvoiceDetailViewModel
import com.pos.cashiersp.presentation.ui.theme.Gray100
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Primary100
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.White


// Purchased Items Section
@Composable
fun PurchasedItemsSection(
    modifier: Modifier = Modifier,
    viewModel: InvoiceDetailViewModel = hiltViewModel()
) {
    val items = viewModel.purchasedItemList.value

    // Adaptive threshold: fewer visible items on small screens so Payment
    // Summary is always reachable without excessive scrolling.
    val screenHeightDp = LocalConfiguration.current.screenHeightDp
    val threshold = if (screenHeightDp < 700) 4 else 6

    var expanded by remember { mutableStateOf(false) }

    // Reset expanded state when item list changes (e.g. navigation)
    LaunchedEffect(items) { expanded = false }

    val visibleItems = remember(items, expanded, threshold) {
        if (expanded || items.size <= threshold) items else items.take(threshold)
    }
    val hiddenCount = items.size - threshold

    Card(
        border = BorderStroke(width = .8.dp, color = Gray100.copy(alpha = .4f)),
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // ── Header ──────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Purchased products",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.W600,
                    color = Secondary,
                )
                // Item count badge
                Box(
                    modifier = Modifier
                        .background(Primary100, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 3.dp),
                ) {
                    Text(
                        text = "${items.size} ${if (items.size == 1) "product" else "products"}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.W600,
                        color = Primary,
                    )
                }
            }

            HorizontalDivider(
                color = Gray100.copy(alpha = .5f),
                thickness = .8.dp,
                modifier = Modifier.padding(horizontal = 14.dp),
            )

            // ── Item rows ───────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
            ) {
                visibleItems.forEachIndexed { index, item ->
                    ItemRow(index = index + 1, item = item)
                    if (index < visibleItems.lastIndex) {
                        HorizontalDivider(
                            color = Gray100.copy(alpha = .4f),
                            thickness = .5.dp,
                        )
                    }
                }
            }

            // ── Show more / Show less button ─────────────────────────────────
            if (items.size > threshold) {
                HorizontalDivider(
                    color = Primary.copy(alpha = .12f),
                    thickness = .8.dp,
                )
                TextButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Primary100.copy(alpha = .5f)),
                    contentPadding = PaddingValues(vertical = 10.dp),
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess
                        else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = if (expanded) "Show less"
                        else "Show $hiddenCount more ${if (hiddenCount == 1) "item" else "items"}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W600,
                        color = Primary,
                    )
                }
            } else {
                Spacer(Modifier.height(14.dp))
            }
        }
    }
}