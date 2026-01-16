package com.pos.cashiersp.presentation.select_store.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pos.cashiersp.model.domain.Store
import com.pos.cashiersp.presentation.select_store.SelectStoreEvent
import com.pos.cashiersp.presentation.select_store.SelectStoreViewModel
import com.pos.cashiersp.presentation.ui.theme.Gray100
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.White
import com.pos.cashiersp.presentation.util.getInitials

@Composable
fun StoreCard(store: Store, viewModel: SelectStoreViewModel = hiltViewModel()) {
    val isSelected = viewModel.selectedStore.value?.id == store.id

    Button(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .border(
                width = 1.dp,
                color = Gray100.copy(alpha = 0.4f),
                shape = RoundedCornerShape(CornerSize(8.dp))
            ),
        colors = ButtonDefaults.buttonColors().copy(containerColor = White),
        contentPadding = PaddingValues(8.dp),
        onClick = { viewModel.onEvent(SelectStoreEvent.OnSelectStoreCard(store)) },
    ) {
        Card(
            colors = CardDefaults.cardColors().copy(
                containerColor = Gray100.copy(alpha = 0.4f)
            ),
            shape = RoundedCornerShape(6.dp),
        ) {
            Text(
                getInitials(store.name),
                color = Secondary,
                fontWeight = FontWeight.W500,
                modifier = Modifier
                    .padding(8.dp)
            )
        }
        Spacer(Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                store.name,
                color = Secondary,
                fontWeight = FontWeight.W400,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            // We could put description later for new feature
            // Text("Staff", fontSize = 13.sp, color = Gray300, fontWeight = FontWeight.W400)
        }

        if (isSelected) {
            Text(
                "Current",
                textAlign = TextAlign.Center,
                fontSize = 10.sp,
                color = White,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Primary)
                    .height(18.dp)
                    .padding(horizontal = 8.dp)
                    .width(IntrinsicSize.Min)
            )
        }
    }
}