package com.pos.cashiersp.presentation.select_tenant.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pos.cashiersp.common.TestTags
import com.pos.cashiersp.presentation.select_tenant.SelectTenantEvent
import com.pos.cashiersp.presentation.select_tenant.SelectTenantViewModel
import com.pos.cashiersp.presentation.ui.theme.Danger
import com.pos.cashiersp.presentation.ui.theme.Gray400
import com.pos.cashiersp.presentation.ui.theme.Gray600
import com.pos.cashiersp.presentation.ui.theme.Light100
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.Warning
import com.pos.cashiersp.presentation.ui.theme.Warning100
import com.pos.cashiersp.presentation.ui.theme.White

@Composable
fun AddNewTenantDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    viewModel: SelectTenantViewModel = hiltViewModel()
) {
    val isLoading = viewModel.isCreatingTenant.value
    val inpTenantName = viewModel.inpNewTenantName.value

    AlertDialog(
        containerColor = White,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.testTag(TestTags.SelectTenantScreen.ADD_NEW_TENANT_DIALOG),
        onDismissRequest = {
            if (!isLoading) onDismissRequest()
        },
        confirmButton = {
            if (!isLoading) {
                TextButton(
                    onClick = onConfirmation,
                    enabled = !isLoading,
                    modifier = Modifier.testTag(TestTags.SelectTenantScreen.CONFIRM_ADD_BUTTON)
                ) {
                    Text(
                        "Confirm",
                        color = Primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = { if (!isLoading) onDismissRequest() },
                enabled = !isLoading,
                modifier = Modifier.testTag(TestTags.SelectTenantScreen.CANCEL_ADD_BUTTON)
            ) {
                Text(
                    "Cancel",
                    color = Gray600
                )
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Tenant Icon",
                tint = Primary,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "Add New Tenant",
                color = Secondary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Description text
                Text(
                    text = "Enter your tenant name (company name) to create a new workspace.",
                    color = Gray600,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                // Warning box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Warning100)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "⚠️",
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Free tier users are limited to a maximum of 3 tenants per account.",
                        color = Warning.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Input field
                OutlinedTextField(
                    value = inpTenantName.text,
                    enabled = !isLoading,
                    onValueChange = {
                        viewModel.onEvent(SelectTenantEvent.EnteredTenantNameInp(it))
                    },
                    label = {
                        Text(
                            "Tenant Name",
                            fontSize = 14.sp
                        )
                    },
                    placeholder = {
                        Text(
                            "e.g., My Company",
                            color = Gray400,
                            fontSize = 14.sp
                        )
                    },
                    isError = inpTenantName.isError,
                    supportingText = {
                        if (inpTenantName.isError) {
                            Text(
                                "Tenant name must be at least 2 characters long.",
                                fontSize = 12.sp
                            )
                        } else {
                            Text(
                                "Minimum 2 characters required",
                                color = Gray400,
                                fontSize = 12.sp
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = White,
                        unfocusedContainerColor = Light100,
                        disabledContainerColor = Light100,
                        cursorColor = Primary,
                        focusedIndicatorColor = Primary,
                        focusedLabelColor = Primary,
                        unfocusedLabelColor = Gray600,
                        errorLabelColor = Danger,
                        errorCursorColor = Danger,
                        errorIndicatorColor = Danger,
                        disabledLabelColor = Gray400,
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TestTags.SelectTenantScreen.TENANT_NAME_INPUT),
                )
            }
        },
    )
}
