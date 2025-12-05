package com.pos.cashiersp.presentation.stock_management.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.pos.cashiersp.common.TestTags

@Composable
fun EditItemStockDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    var email by remember { mutableStateOf("") }

    val emailHasErrors by remember {
        derivedStateOf {
            if (email.isNotEmpty()) {
                // Email is considered erroneous until it completely matches EMAIL_ADDRESS.
                //!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                true
            } else {
                false
            }
        }
    }

    fun updateEmail(input: String) {
        email = input
    }

    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            //Text(text = dialogText)
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                value = email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { input -> updateEmail(input) },
                label = { Text("Enter stock") },
                isError = emailHasErrors,
                supportingText = {
                    if (emailHasErrors) {
                        Text("Incorrect email format.")
                    }
                }
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Cancel")
            }
        }, modifier = Modifier.testTag(TestTags.StockManagementScreen.EDIT_STOCK_DIALOG)
    )
}