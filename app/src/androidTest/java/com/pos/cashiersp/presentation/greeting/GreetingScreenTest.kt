package com.pos.cashiersp.presentation.greeting

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pos.cashiersp.common.TestTags
import com.pos.cashiersp.di.RetrofitModule
import com.pos.cashiersp.di.RoomModule
import com.pos.cashiersp.presentation.MainActivity
import com.pos.cashiersp.presentation.Screen
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
class GreetingScreenTest {
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
                                "data": {
                                    "members": [
                                        {
                                            "email": "greeting@gmail.com",
                                            "id": 1,
                                            "name": "greeting saputra",
                                            "created_at": "2025-08-01T02:48:30.942816Z"
                                        }
                                    ],
                                    "requestedTenant": 1,
                                    "requestedBy": 1
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
                    startDestination = Screen.GREETING
                ) {
                    composable(route = Screen.GREETING) {
                        GreetingScreen(navController)
                    }
                }
            }
        }
    }

    @Test
    fun test_isVisible() {
        val greetingComponent = composeRule.onNodeWithTag(TestTags.GREETING_TEXT)
        greetingComponent.assertExists()
        greetingComponent.assertIsDisplayed()
        greetingComponent.assertTextEquals("Hello Testing!")
    }

    @After
    fun teardown() {
        mockServer.shutdown()
    }
}