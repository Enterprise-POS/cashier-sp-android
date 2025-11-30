package com.pos.cashiersp.presentation.greeting

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavController
import com.pos.cashiersp.common.TestTags
import com.pos.cashiersp.presentation.ui.theme.Primary
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun GreetingScreen(navController: NavController, viewModel: GreetingViewModel = hiltViewModel()) {
    val state = viewModel.state
    val members = viewModel.members

    println(state.value.error)
    println(members)
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Greeting(
            name = "Testing",
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        color = Color(Primary.value),
        modifier = modifier.testTag(TestTags.GREETING_TEXT)
    )
}

