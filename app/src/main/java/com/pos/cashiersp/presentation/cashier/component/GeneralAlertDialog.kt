package com.pos.cashiersp.presentation.cashier.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pos.cashiersp.presentation.ui.theme.Danger
import com.pos.cashiersp.presentation.ui.theme.Danger100
import com.pos.cashiersp.presentation.ui.theme.Gray600
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.Success
import com.pos.cashiersp.presentation.ui.theme.Success100
import com.pos.cashiersp.presentation.ui.theme.White

data class GeneralAlertDialogStatus(
    val isLoading: Boolean = false,
    val loadingMessage: String? = null,

    val errorTitle: String? = null,
    val errorMessage: String? = null,

    val successTitle: String? = null,
    val successMessage: String? = null,

    val status: Boolean = true,

    // This will handle the state
    val showDialog: Boolean = false,
) {
    companion object {
        fun success(successTitle: String, successMessage: String): GeneralAlertDialogStatus {
            return GeneralAlertDialogStatus(
                status = true,
                isLoading = false,
                showDialog = true,
                successTitle = successTitle,
                successMessage = successMessage,
            )
        }

        fun error(errorTitle: String, errorMessage: String): GeneralAlertDialogStatus {
            return GeneralAlertDialogStatus(
                status = false,
                isLoading = false,
                showDialog = true,
                errorTitle = errorTitle,
                errorMessage = errorMessage
            )
        }

        fun loading(loadingMessage: String = ""): GeneralAlertDialogStatus {
            return GeneralAlertDialogStatus(
                status = false,
                isLoading = true,
                showDialog = true,
                loadingMessage = loadingMessage
            )
        }
    }
}


@Composable
fun GeneralAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    generalAlertDialogStatus: GeneralAlertDialogStatus,
) {
    val status = generalAlertDialogStatus.status
    val title = if (status) generalAlertDialogStatus.successTitle else generalAlertDialogStatus.errorTitle
    val message = if (status) generalAlertDialogStatus.successMessage else generalAlertDialogStatus.errorMessage

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onConfirmation,
            ) {
                Text(
                    "Confirm",
                    color = Primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
        },
        icon = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (status)
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Check Circle Icon",
                        tint = Success,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Success100)
                            .padding(12.dp)
                    )
                else
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Alert Icon",
                        tint = Danger,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Danger100)
                            .padding(12.dp)
                    )
            }
        },
        title = {
            Text(
                text = if (generalAlertDialogStatus.isLoading) "Please wait" else title!!,
                color = Secondary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (generalAlertDialogStatus.isLoading) generalAlertDialogStatus.loadingMessage!! else message!!,
                    color = Gray600,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center
                )
            }
        },
        containerColor = White,
        shape = RoundedCornerShape(16.dp)
    )
}