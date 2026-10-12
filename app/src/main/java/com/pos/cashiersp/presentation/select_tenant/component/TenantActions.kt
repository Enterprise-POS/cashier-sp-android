package com.pos.cashiersp.presentation.select_tenant.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.pos.cashiersp.common.TestTags
import com.pos.cashiersp.presentation.ui.theme.Gray300
import com.pos.cashiersp.presentation.ui.theme.Light
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.White

@Composable
fun TenantActions(
    isLoading: Boolean,
    hasSelectedTenant: Boolean,
    onContinue: () -> Unit,
    onSwitchAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier
    ) {
        TextButton(
            enabled = !isLoading && hasSelectedTenant,
            colors = ButtonDefaults.textButtonColors(
                containerColor = Primary,
                contentColor = White
            ),
            shape = RoundedCornerShape(corner = CornerSize(12.dp)),
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .testTag(TestTags.SelectTenantScreen.CONTINUE_BUTTON),
            onClick = onContinue,
        ) {
            Text("Continue")
        }
        Spacer(Modifier.height(4.dp))
        TextButton(
            onClick = onSwitchAccount,
            colors = ButtonDefaults.textButtonColors(
                containerColor = Color.Transparent,
                contentColor = Secondary,
                disabledContentColor = Gray300,
                disabledContainerColor = Light
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .testTag(TestTags.SelectTenantScreen.SWITCH_ACCOUNT_BUTTON)
        ) {
            Text("Switch account")
        }
        Spacer(Modifier.height(10.dp))
    }
}