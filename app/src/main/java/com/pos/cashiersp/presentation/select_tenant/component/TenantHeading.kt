package com.pos.cashiersp.presentation.select_tenant.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.sp
import com.pos.cashiersp.common.TestTags
import com.pos.cashiersp.presentation.ui.theme.Gray600
import com.pos.cashiersp.presentation.ui.theme.Secondary

@Composable
fun TenantHeading() {
    Text(
        "Select a tenant",
        fontSize = 28.sp,
        color = Secondary,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(TestTags.SelectTenantScreen.SELECT_TENANT_TITLE)
    )
    Text(
        "Choose tenant that you want to manage",
        fontSize = 12.sp,
        color = Gray600,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(TestTags.SelectTenantScreen.SELECT_TENANT_TITLE_SUB_TITLE)
    )
}
