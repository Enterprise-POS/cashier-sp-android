package com.pos.cashiersp.presentation.login_register.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.pos.cashiersp.common.TestTags
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.White

enum class LoginOrRegisterSegmentedButtonPage {
    Login,
    Register,
}

@Composable
fun LoginOrRegisterSegmentedButton(
    modifier: Modifier = Modifier,
    initialValue: LoginOrRegisterSegmentedButtonPage,
    onChanged: (v: LoginOrRegisterSegmentedButtonPage) -> Unit,
) {
    val options = listOf<LoginOrRegisterSegmentedButtonPage>(
        LoginOrRegisterSegmentedButtonPage.Login,
        LoginOrRegisterSegmentedButtonPage.Register
    )
    var selectedIndex by remember { mutableIntStateOf(options.indexOf(initialValue)) }

    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp),
    ) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                modifier = Modifier.testTag(TestTags.LoginRegisterScreen.SEGMENTED_BUTTON),
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size,
                    baseShape = SegmentedButtonDefaults.baseShape.copy(
                        all = CornerSize(8.dp)
                    ),
                ),
                onClick = {
                    selectedIndex = index
                    onChanged(options[index]) // Will lift up the state
                },
                selected = index == selectedIndex,
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = Primary,
                    activeBorderColor = Secondary,
                    activeContentColor = White,
                    inactiveBorderColor = Secondary,
                    inactiveContainerColor = Secondary,
                    inactiveContentColor = White,
                ),
                border = BorderStroke(width = 2.dp, Secondary),
                label = { Text(label.name, color = White) },
                icon = {
                    when (label) {
                        LoginOrRegisterSegmentedButtonPage.Login -> Icon(
                            imageVector = Icons.Outlined.PlayArrow,
                            contentDescription = "Search icon, if the icon touched that mean the user already finished",
                            modifier = Modifier
                                .size(28.dp)
                        )

                        LoginOrRegisterSegmentedButtonPage.Register -> {
                            Icon(
                                imageVector = Icons.Outlined.Create,
                                contentDescription = "Register icon",
                                modifier = Modifier
                                    .size(28.dp)
                            )
                        }
                    }
                }
            )
        }
    }
}