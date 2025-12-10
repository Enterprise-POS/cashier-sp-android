package com.pos.cashiersp.presentation.select_tenant

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pos.cashiersp.common.TestTags
import com.pos.cashiersp.di.AppModule
import com.pos.cashiersp.di.RetrofitModule
import com.pos.cashiersp.di.RoomModule
import com.pos.cashiersp.model.dto.User
import com.pos.cashiersp.presentation.MainActivity
import com.pos.cashiersp.presentation.Screen
import com.pos.cashiersp.presentation.ui.theme.CashierSPTheme
import com.pos.cashiersp.presentation.util.JwtStore
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(RetrofitModule::class, RoomModule::class, AppModule::class)
class SelectTenantScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var mockServer: MockWebServer

    @Inject
    lateinit var jwtStore: JwtStore

    @Before
    fun setUp() = runBlocking {
        hiltRule.inject()

        jwtStore.clearToken()
        jwtStore.saveToken(
            token = "TEST_TOKEN",
            user = User("johndoe@gmail.com", 1, "John Doe", "2025-12-07T14:42:26.517672Z")
        )

        mockServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return when (request.path) {
                    "/tenants/members/1" -> MockResponse()
                        .setResponseCode(200)
                        .setBody(
                            """
                            {
                                "code": 200,
                                "status": "success",
                                "data": {
                                    
                                }
                            }
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
            val navController = rememberNavController()
            CashierSPTheme {
                NavHost(
                    navController = navController,
                    startDestination = Screen.SELECT_TENANT
                ) {
                    composable(route = Screen.SELECT_TENANT) {
                        SelectTenantScreen(navController)
                    }
                }
            }
        }
    }

    @Test
    fun selectTenantScreenTitleAndSub_isVisible() {
        composeRule.onNodeWithTag(TestTags.SelectTenantScreen.SELECT_TENANT_TITLE)
            .assertExists()
            .assertIsDisplayed()
            .assertTextEquals("Select a tenant")

        composeRule.onNodeWithTag(TestTags.SelectTenantScreen.SELECT_TENANT_TITLE_SUB_TITLE)
            .assertExists()
            .assertIsDisplayed()
            .assertTextEquals("Choose tenant that you want to manage")
    }

    @Test
    fun selectTenantScreenAllButtons_isClickable() {
        composeRule.onNodeWithTag(TestTags.SelectTenantScreen.ADD_TENANT_BUTTON)
            .assertExists()
            .assertIsDisplayed()
            .assertHasClickAction()

        composeRule.onNodeWithTag(TestTags.SelectTenantScreen.CONTINUE_BUTTON)
            .assertExists()
            .assertIsDisplayed()
            .assertHasClickAction()
            .assert(hasText("Continue"))

        composeRule.onNodeWithTag(TestTags.SelectTenantScreen.SWITCH_ACCOUNT_BUTTON)
            .assertExists()
            .assertIsDisplayed()
            .assertHasClickAction()
            .assert(hasText("Switch account"))
    }

    @After
    fun teardown() {
        mockServer.shutdown()
    }
}