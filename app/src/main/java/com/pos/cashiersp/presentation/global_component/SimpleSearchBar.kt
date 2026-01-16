package com.pos.cashiersp.presentation.global_component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Primary100
import com.pos.cashiersp.presentation.ui.theme.Primary200
import com.pos.cashiersp.presentation.ui.theme.Secondary300
import com.pos.cashiersp.presentation.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSearchBar(
    modifier: Modifier = Modifier,
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    onClear: () -> Unit = {},
    searchResults: List<String>,
    enabled: Boolean = false,
) {
    // Controls expansion state of the search bar
    var expanded by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier
            .padding(0.dp)
            .fillMaxWidth()
            .semantics() { isTraversalGroup = true },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        SearchBar(
            windowInsets = WindowInsets(top = 0), // Will remove top inner padding
            modifier = Modifier
                //.background(Primary) // For debug
                .offset(y = (-6).dp)
                .semantics { traversalIndex = 0f },
            colors = SearchBarDefaults.colors(
                containerColor = White,
                dividerColor = Secondary300,
            ),
            inputField = {
                SearchBarDefaults.InputField(
                    enabled = enabled,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = White,
                        cursorColor = Primary,
                        unfocusedContainerColor = White.copy(.8f),
                    ),
                    modifier = Modifier
                        .fillMaxWidth(.96f),
                    query = textFieldState.text.toString(),
                    onQueryChange = { textFieldState.edit { replace(0, length, it) } },
                    onSearch = {
                        textFieldState.edit { replace(0, length, it) }
                        onSearch(textFieldState.text.toString())
                        expanded = false
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    leadingIcon = {
                        if (textFieldState.text.isNotEmpty()) {
                            IconButton(onClick = {
                                textFieldState.edit { replace(0, length, "") }
                                onClear()
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                textFieldState.edit { replace(0, length, textFieldState.text.toString()) }
                                onSearch(textFieldState.text.toString())
                                expanded = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Search icon, if the icon touched that mean the user already finished",
                                modifier = Modifier
                                    .size(28.dp)
                            )
                        }
                    },
                    placeholder = { Text("Search") }
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            // Display search results in a scrollable column
            Row {
                // TODO: maybe make it more flexible to edit so it can be global component
                //MinimalDropdownMenu()
                //MinimalDropdownMenu()
            }
            searchResults.forEach { result ->
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Primary200),
                    headlineContent = { Text(result) },
                    modifier = Modifier
                        .clickable {
                            // TODO: implement this
                            textFieldState.edit { replace(0, length, result) }
                            expanded = false
                        }
                        .fillMaxWidth()
                )
            }
        }
    }
}
