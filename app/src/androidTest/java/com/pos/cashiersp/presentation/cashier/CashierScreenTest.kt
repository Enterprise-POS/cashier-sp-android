package com.pos.cashiersp.presentation.cashier

import androidx.compose.material3.DrawerState
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
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
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(RetrofitModule::class, RoomModule::class)
class CashierScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var mockServer: MockWebServer

    @Before
    fun setUp() {
        hiltRule.inject()

        mockServer.dispatcher = object : okhttp3.mockwebserver.Dispatcher() {
            override fun dispatch(request: okhttp3.mockwebserver.RecordedRequest): MockResponse {
                return when (request.path) {
                    "/tenants/members/1" -> MockResponse()
                        .setResponseCode(200)
                        .setBody(
                            """
                            {
                                "code": 200,
                                "status": "success",
                                "data": {}
                        """.trimIndent()
                        )

                    else -> {
                        println("Unmatched path: ${request.path}")
                        MockResponse().setResponseCode(500)
                    }
                }
            }
        }

        mockServer.start()

        composeRule.setContent {
            CashierSPTheme {
                val navController: NavHostController = rememberNavController()
                CashierDrawer { drawerState: DrawerState ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.CASHIER
                    ) {
                        composable(route = Screen.CASHIER) {
                            CashierScreen(navController, drawerState)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun titleText_withCashierTitle() {
        val greetingComponent = composeRule.onNodeWithTag(TestTags.CashierScreen.CASHIER_TITLE)
        greetingComponent.assertExists()
        greetingComponent.assertIsDisplayed()
        greetingComponent.assertTextEquals("Cashier")
    }

    @Test
    fun button_withMenuIcon_openCashierDrawer() {
        val menuIconButton = composeRule.onNodeWithTag(TestTags.CashierScreen.MENU_DRAWER_BUTTON)
        menuIconButton.assertExists()
        menuIconButton.isDisplayed()
        menuIconButton.assertHasClickAction()

        menuIconButton.performClick()
        composeRule.waitForIdle()
        val cashierDrawerContainer = composeRule.onNodeWithTag(TestTags.DRAWER_CONTAINER)
        cashierDrawerContainer.assertExists()
        cashierDrawerContainer.isDisplayed()
    }

    @Test
    fun button_withChartIcon_openCashierBottomSheet() {
        val chartIconButton = composeRule.onNodeWithTag(TestTags.CashierScreen.CHART_BUTTON)
        chartIconButton.assertExists()
        chartIconButton.isDisplayed()
        chartIconButton.assertHasClickAction()

        chartIconButton.performClick()
        composeRule.waitForIdle()
        val cashierBottomSheetContainer = composeRule.onNodeWithTag(TestTags.CashierScreen.PAYMENT_BOTTOM_SHEET)
        cashierBottomSheetContainer.assertExists()
        cashierBottomSheetContainer.isDisplayed()
    }

    @After
    fun teardown() {
        mockServer.shutdown()
    }
}