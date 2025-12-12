package com.pos.cashiersp.presentation.select_tenant.component

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pos.cashiersp.common.TestTags
import com.pos.cashiersp.model.dto.Tenant
import com.pos.cashiersp.presentation.select_tenant.SelectTenantEvent
import com.pos.cashiersp.presentation.select_tenant.SelectTenantViewModel
import com.pos.cashiersp.presentation.ui.theme.Gray300
import com.pos.cashiersp.presentation.ui.theme.Light
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.White
import com.pos.cashiersp.presentation.util.getInitials

@Composable
fun TenantCard(tenant: Tenant, viewModel: SelectTenantViewModel = hiltViewModel()) {
    val isSelected = viewModel.selectedTenant.value?.id == tenant.id

    Button(
        shape = RoundedCornerShape(CornerSize(8.dp)),
        modifier = Modifier
            .padding(vertical = 4.dp)
            .testTag(TestTags.SelectTenantScreen.TENANT_CARD_BUTTON),
        colors = ButtonDefaults.buttonColors().copy(containerColor = Light),
        contentPadding = PaddingValues(8.dp),
        onClick = { viewModel.onEvent(SelectTenantEvent.OnClickTenantCard(tenant)) },
    ) {
        Card(
            colors = CardDefaults.cardColors().copy(
                containerColor = Secondary
            ),
            shape = RoundedCornerShape(6.dp),
        ) {
            Text(
                getInitials(tenant.name),
                color = White,
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
                tenant.name,
                color = Secondary,
                fontWeight = FontWeight.W400,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text("Staff", fontSize = 13.sp, color = Gray300, fontWeight = FontWeight.W400)
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
