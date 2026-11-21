package com.pos.cashiersp.presentation.expenses.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pos.cashiersp.model.domain.ExpenseCategory
import com.pos.cashiersp.presentation.expenses.CategoryManagerState
import com.pos.cashiersp.presentation.expenses.ExpenseEvent
import com.pos.cashiersp.presentation.ui.theme.Danger
import com.pos.cashiersp.presentation.ui.theme.Gray600
import com.pos.cashiersp.presentation.ui.theme.Primary

/** Add / rename / delete the tenant's expense categories. */
@Composable
fun ExpenseCategoryDialog(
    state: CategoryManagerState,
    categories: List<ExpenseCategory>,
    onEvent: (ExpenseEvent) -> Unit,
) {
    var confirmDelete by remember { mutableStateOf<ExpenseCategory?>(null) }

    AlertDialog(
        onDismissRequest = { onEvent(ExpenseEvent.OnSetCategoryManager(false)) },
        title = { Text("Expense Categories") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                state.error?.let { Text(it, color = Danger, fontSize = 13.sp) }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = state.newName,
                        onValueChange = { onEvent(ExpenseEvent.OnChangeNewCategoryName(it)) },
                        placeholder = { Text("New category name") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                    Button(
                        onClick = { onEvent(ExpenseEvent.OnAddCategory) },
                        enabled = !state.isBusy && state.newName.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    ) { Text("Add") }
                }

                if (categories.isEmpty()) {
                    Text("No categories yet. Add your first one above.", color = Gray600, fontSize = 13.sp)
                }
                LazyColumn(modifier = Modifier.heightIn(max = 320.dp)) {
                    items(categories, key = { it.id }) { category ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            if (state.renamingId == category.id) {
                                // Move the keyboard to the rename field, not the "new category" field above
                                val focusRequester = remember { FocusRequester() }
                                LaunchedEffect(category.id) { focusRequester.requestFocus() }
                                // Cursor starts at the end of the current name
                                var renameValue by remember(category.id) {
                                    mutableStateOf(TextFieldValue(state.renamingName, TextRange(state.renamingName.length)))
                                }
                                OutlinedTextField(
                                    value = renameValue,
                                    onValueChange = {
                                        renameValue = it
                                        onEvent(ExpenseEvent.OnChangeRenameCategory(it.text))
                                    },
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .focusRequester(focusRequester),
                                )
                                IconButton(
                                    onClick = { onEvent(ExpenseEvent.OnSaveRenameCategory) },
                                    enabled = !state.isBusy && state.renamingName.isNotBlank(),
                                ) { Icon(Icons.Outlined.Check, contentDescription = "Save name") }
                                IconButton(onClick = { onEvent(ExpenseEvent.OnStartRenameCategory(null)) }) {
                                    Icon(Icons.Outlined.Close, contentDescription = "Cancel rename")
                                }
                            } else {
                                Text(category.name, modifier = Modifier.weight(1f))
                                IconButton(
                                    onClick = { onEvent(ExpenseEvent.OnStartRenameCategory(category.id, category.name)) },
                                    enabled = !state.isBusy,
                                ) { Icon(Icons.Outlined.Edit, contentDescription = "Rename ${category.name}") }
                                IconButton(onClick = { confirmDelete = category }, enabled = !state.isBusy) {
                                    Icon(Icons.Outlined.Delete, contentDescription = "Delete ${category.name}", tint = Danger)
                                }
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onEvent(ExpenseEvent.OnSetCategoryManager(false)) }) { Text("Done") }
        },
    )

    confirmDelete?.let { category ->
        AlertDialog(
            onDismissRequest = { confirmDelete = null },
            title = { Text("Delete '${category.name}'?") },
            text = { Text("Its expenses will stay, as Uncategorized.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = null
                    onEvent(ExpenseEvent.OnDeleteCategory(category.id))
                }) { Text("Delete", color = Danger) }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = null }) { Text("Cancel") } },
        )
    }
}
