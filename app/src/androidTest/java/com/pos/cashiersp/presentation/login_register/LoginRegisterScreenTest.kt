package com.pos.cashiersp.presentation.login_register

import androidx.compose.material3.DrawerState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pos.cashiersp.common.TestTags
import com.pos.cashiersp.di.RetrofitModule
import com.pos.cashiersp.di.RoomModule
import com.pos.cashiersp.presentation.MainActivity
import com.pos.cashiersp.presentation.Screen
import com.pos.cashiersp.presentation.global_component.CashierDrawer
import com.pos.cashiersp.presentation.ui.theme.CashierSPTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(RetrofitModule::class, RoomModule::class)
class LoginRegisterScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var mockServer: MockWebServer

    @Before
    fun setUp() {
        hiltRule.inject()

        mockServer.start()

        composeRule.setContent {
            CashierSPTheme {
                val navController: NavHostController = rememberNavController()
                CashierDrawer(navController) { drawerState: DrawerState ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.LOGIN_REGISTER
                    ) {
                        composable(route = Screen.LOGIN_REGISTER) {
                            LoginRegisterScreen(navController)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun titleText_displayingWelcome_whenFirstOpen() {
        composeRule.waitForIdle()
        val screenTitle = composeRule.onNodeWithTag(TestTags.LoginRegisterScreen.LOGIN_REGISTER_TITLE)
        screenTitle.assertExists()
        screenTitle.assertIsDisplayed()
        screenTitle.assertTextEquals("Welcome back")

        val screenSubTitle = composeRule.onNodeWithTag(TestTags.LoginRegisterScreen.LOGIN_REGISTER_SUB_TITLE)
        screenSubTitle.assertExists()
        screenSubTitle.assertIsDisplayed()
        screenSubTitle.assertTextEquals("Sign in to start a new sale or manage your store")
    }

    @Test
    fun titleText_changeToHello_whenSegmentedButtonClicked() {
        val registerSegmentedButton = composeRule.onNodeWithText("Register")
        registerSegmentedButton.assertExists()
        registerSegmentedButton.assertIsDisplayed()
        registerSegmentedButton.performClick()

        val screenTitle = composeRule.onNodeWithTag(TestTags.LoginRegisterScreen.LOGIN_REGISTER_TITLE)
        screenTitle.assertExists()
        screenTitle.assertIsDisplayed()
        screenTitle.assertTextEquals("Hello !")

        val screenSubTitle = composeRule.onNodeWithTag(TestTags.LoginRegisterScreen.LOGIN_REGISTER_SUB_TITLE)
        screenSubTitle.assertExists()
        screenSubTitle.assertIsDisplayed()
        screenSubTitle.assertTextEquals("Create new account and start manage your store")
    }

    @After
    fun teardown() {
        mockServer.shutdown()
    }
}