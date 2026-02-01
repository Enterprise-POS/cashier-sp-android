package com.pos.cashiersp.presentation.transaction_history.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pos.cashiersp.presentation.global_component.TextWithNoPadding
import com.pos.cashiersp.presentation.ui.theme.Gray100
import com.pos.cashiersp.presentation.ui.theme.Gray400
import com.pos.cashiersp.presentation.ui.theme.Secondary

@Composable
fun PaymentSummaryCard(
    modifier: Modifier = Modifier,
    totalAmount: String = "$2,345.00",
    transactionCount: Int = 32,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF4E6)
            ),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(width = .8.dp, color = Gray100.copy(alpha = .4f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,

                ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Total collected",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Secondary
                    )
                    Text(
                        text = "Complete payments in selected range.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Gray400
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TextWithNoPadding(
                        text = totalAmount,
                        fontWeight = FontWeight.SemiBold,
                        color = Secondary
                    )
                    Text(
                        text = "32 transactions",
                        style = MaterialTheme.typography.bodySmall,
                        color = Gray400
                    )
                }
            }
        }
    }
}