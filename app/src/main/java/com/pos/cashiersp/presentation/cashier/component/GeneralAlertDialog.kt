package com.pos.cashiersp.presentation.cashier.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

/**
 * Represents the state of a general alert dialog.
 *
 * @property type The type of dialog (SUCCESS, ERROR, or LOADING)
 * @property title The dialog title
 * @property message The dialog message
 * @property showDialog Whether the dialog should be shown
 */
data class GeneralAlertDialogStatus(
    val type: DialogType = DialogType.SUCCESS,
    val title: String = "",
    val message: String = "",
    val showDialog: Boolean = false,
) {
    enum class DialogType {
        SUCCESS,
        ERROR,
        LOADING
    }

    val isLoading: Boolean get() = type == DialogType.LOADING
    val status: Boolean get() = type == DialogType.SUCCESS

    companion object {
        fun success(title: String, message: String): GeneralAlertDialogStatus {
            return GeneralAlertDialogStatus(
                type = DialogType.SUCCESS,
                title = title,
                message = message,
                showDialog = true,
            )
        }

        fun error(title: String, message: String): GeneralAlertDialogStatus {
            return GeneralAlertDialogStatus(
                type = DialogType.ERROR,
                title = title,
                message = message,
                showDialog = true,
            )
        }

        fun loading(message: String = "Please wait..."): GeneralAlertDialogStatus {
            return GeneralAlertDialogStatus(
                type = DialogType.LOADING,
                title = "Loading",
                message = message,
                showDialog = true,
            )
        }
    }
}

@Composable
fun GeneralAlertDialog(
    generalAlertDialogStatus: GeneralAlertDialogStatus,
    confirmText: String = "",
    cancelText: String = "",
    onDismissRequest: () -> Unit = {},
    onConfirmation: (() -> Unit)? = null,
) {
    val dialogConfig = when (generalAlertDialogStatus.type) {
        GeneralAlertDialogStatus.DialogType.SUCCESS -> DialogConfig(
            icon = Icons.Default.CheckCircle,
            iconTint = Success,
            iconBackground = Success100,
            showIcon = true
        )

        GeneralAlertDialogStatus.DialogType.ERROR -> DialogConfig(
            icon = Icons.Default.Info,
            iconTint = Danger,
            iconBackground = Danger100,
            showIcon = true
        )

        GeneralAlertDialogStatus.DialogType.LOADING -> DialogConfig(
            icon = Icons.Default.Info,
            iconTint = Primary,
            iconBackground = Primary.copy(alpha = 0.1f),
            showIcon = false
        )
    }

    AlertDialog(
        onDismissRequest = if (generalAlertDialogStatus.isLoading) {
            {} // Prevent dismissing while loading
        } else {
            onDismissRequest
        },
        icon = {
            DialogIcon(
                config = dialogConfig,
                isLoading = generalAlertDialogStatus.isLoading
            )
        },
        title = {
            Text(
                text = generalAlertDialogStatus.title,
                color = Secondary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = generalAlertDialogStatus.message,
                color = Gray600,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        containerColor = White,
        shape = RoundedCornerShape(16.dp),
        confirmButton = {
            DialogButtons(
                isLoading = generalAlertDialogStatus.isLoading,
                onConfirmation = onConfirmation,
                onDismissRequest = onDismissRequest,
                cancelText = cancelText,
                confirmText = confirmText
            )
        },
    )
}

@Composable
private fun DialogIcon(
    config: DialogConfig,
    isLoading: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Primary,
                modifier = Modifier.size(48.dp)
            )
        } else if (config.showIcon) {
            Icon(
                imageVector = config.icon,
                contentDescription = "${config.icon.name} Icon",
                tint = config.iconTint,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(config.iconBackground)
                    .padding(12.dp)
            )
        }
    }
}

@Composable
private fun DialogButtons(
    isLoading: Boolean,
    confirmText: String,
    cancelText: String,
    onConfirmation: (() -> Unit)?,
    onDismissRequest: () -> Unit
) {
    if (!isLoading) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            // Show dismiss button if there's no confirmation action
            if (onConfirmation == null) {
                TextButton(onClick = onDismissRequest) {
                    Text("OK", color = Primary)
                }
            } else {
                // Show both buttons if there's a confirmation action
                TextButton(onClick = onDismissRequest) {
                    Text(if (cancelText.isNotEmpty()) cancelText else "Cancel", color = Gray600)
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onConfirmation) {
                    Text(if (confirmText.isNotEmpty()) confirmText else "Confirm", color = Primary)
                }
            }
        }
    }
}

private data class DialogConfig(
    val icon: ImageVector,
    val iconTint: Color,
    val iconBackground: Color,
    val showIcon: Boolean
)