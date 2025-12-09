package com.pos.cashiersp.presentation.stock_management

import androidx.compose.material3.DrawerState
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pos.cashiersp.common.TestTags
import com.pos.cashiersp.di.AppModule
import com.pos.cashiersp.di.RetrofitModule
import com.pos.cashiersp.di.RoomModule
import com.pos.cashiersp.presentation.MainActivity
import com.pos.cashiersp.presentation.Screen
import com.pos.cashiersp.presentation.cashier.CashierScreen
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
@UninstallModules(RetrofitModule::class, RoomModule::class, AppModule::class)
class StockManagementScreenTest {
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
                CashierDrawer(navController) { drawerState: DrawerState ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.STOCK_MANAGEMENT
                    ) {
                        composable(route = Screen.STOCK_MANAGEMENT) {
                            StockManagementScreen(drawerState)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun title_isStockManagement() {
        val topBar = composeRule.onNodeWithTag(TestTags.StockManagementScreen.STOCK_MANAGEMENT_TITLE)
        topBar.assertExists()
        topBar.assertIsDisplayed()
        topBar.assertTextEquals(Screen.STOCK_MANAGEMENT)
    }

    @Test
    fun whenFirstOpen_displayHeaderTitle() {
        val headerTitle = listOf("Total Products", "Low Stock", "Out of Stock")
        headerTitle.forEach {
            val menuIconButton = composeRule.onNodeWithText(it)
            menuIconButton.assertExists()
            menuIconButton.isDisplayed()
        }
    }

    @Test
    fun nextAndPreviousButton_clickable() {
        val nextListPageBtn =
            composeRule.onNodeWithTag(TestTags.StockManagementScreen.NEXT_LIST_PAGE_BUTTON)
        val prevListPageBtn =
            composeRule.onNodeWithTag(TestTags.StockManagementScreen.PREVIOUS_LIST_PAGE_BUTTON)

        listOf(nextListPageBtn, prevListPageBtn).forEach {
            it.assertExists()
            it.assertIsDisplayed()
            it.assertHasClickAction()
        }
    }

    @After
    fun teardown() {
        mockServer.shutdown()
    }
}