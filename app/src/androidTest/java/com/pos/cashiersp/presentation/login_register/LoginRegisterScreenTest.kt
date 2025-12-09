package com.pos.cashiersp.presentation.login_register

import androidx.compose.material3.DrawerState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
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
import com.pos.cashiersp.presentation.global_component.CashierDrawer
import com.pos.cashiersp.presentation.ui.theme.CashierSPTheme
import com.pos.cashiersp.presentation.util.JwtStore
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.HttpURLConnection
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(RetrofitModule::class, RoomModule::class, AppModule::class)
class LoginRegisterScreenTest {
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

        // runBlocking will allow jwtStore to clear DataStore
        jwtStore.clearToken()

        // The use of '/' is required otherwise path not found
        mockServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return when (request.path) {
                    "/users/sign_in" -> MockResponse()
                        .setResponseCode(HttpURLConnection.HTTP_CREATED)
                        .setBody(
                            """
                            {
                                "code": 200,
                                "status": "success",
                                "data": {
                                    "token": "User-Sign-In-JWT",
                                    "user": {
                                        "id": 1,
                                        "name": "John Doe",
                                        "email": "fromsp@gmail.com",
                                        "created_at": "2025-08-01T02:48:30.942816Z"
                                    }
                                }
                            }
                        """.trimIndent()
                        )

                    "/users/sign_up" -> MockResponse()
                        .setResponseCode(HttpURLConnection.HTTP_OK)
                        .setBody(
                            """
                            {
                                "code": 200,
                                "status": "success",
                                "data": {
                                    "token": "User-Sign-Up-JWT",
                                    "user": {
                                        "id": 1,
                                        "name": "John Doe",
                                        "email": "fromsp@gmail.com",
                                        "created_at": "2025-12-07T14:42:26.517672Z"
                                    }
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
                        composable(route = Screen.SELECT_TENANT) {
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

    @Test
    fun emailAndPasswordField_acceptsInput() {
        val emailInpField = composeRule.onNodeWithTag(TestTags.LoginRegisterScreen.LOGIN_EMAIL_INPUT)
        emailInpField.assertExists()
            .assertIsDisplayed()
            .performTextInput("John Doe")
        emailInpField.assertTextEquals("John Doe")

        val passwordInpField = composeRule.onNodeWithTag(TestTags.LoginRegisterScreen.LOGIN_PASSWORD_INPUT)
        passwordInpField.assertExists()
            .assertIsDisplayed()
            .performTextInput("12345678")
        passwordInpField.assertTextEquals("••••••••")
    }

    @Test
    fun loginSide_signIn() = runTest {
        val emailInpField = composeRule.onNodeWithTag(TestTags.LoginRegisterScreen.LOGIN_EMAIL_INPUT)
        emailInpField.assertExists()
            .assertIsDisplayed()
            .performTextInput("fromsp@gmail.com")

        val passwordInpField = composeRule.onNodeWithTag(TestTags.LoginRegisterScreen.LOGIN_PASSWORD_INPUT)
        passwordInpField.assertExists()
            .assertIsDisplayed()
            .performTextInput("12345678")

        val continueBtn = composeRule.onNodeWithTag(TestTags.LoginRegisterScreen.CONTINUE_BUTTON)
        continueBtn.assertExists()
            .assertIsDisplayed()
            .performClick()

        composeRule.waitForIdle()

        composeRule.waitUntil(5_000) {
            runBlocking {
                jwtStore.getPayload().first() != null
            }
        }

        val payload = jwtStore.getPayload().filterNotNull().first()
        Assert.assertEquals(1, payload.sub)
        Assert.assertEquals("John Doe", payload.name)
    }

    @Test
    fun registerSide_signUp() = runTest {
        composeRule.onNodeWithText("Register")
            .assertExists()
            .assertIsDisplayed()
            .performClick()

        val expectedUserID = 1
        val expectedEmail = "fromsp@gmail.com"
        val expectedUsername = "John Doe"
        val password1And2 = "12345678"

        val emailInpField = composeRule.onNodeWithTag(TestTags.LoginRegisterScreen.REGISTER_EMAIL)
        emailInpField.assertExists()
            .assertIsDisplayed()
            .performTextInput(expectedEmail)

        val usernameInpField = composeRule.onNodeWithTag(TestTags.LoginRegisterScreen.REGISTER_USERNAME)
        usernameInpField.assertExists()
            .assertIsDisplayed()
            .performTextInput(expectedUsername)

        val passwordInpField = composeRule.onNodeWithTag(TestTags.LoginRegisterScreen.REGISTER_PASSWORD)
        passwordInpField.assertExists()
            .assertIsDisplayed()
            .performTextInput(password1And2)

        val password2InpField = composeRule.onNodeWithTag(TestTags.LoginRegisterScreen.REGISTER_PASSWORD2)
        password2InpField.assertExists()
            .assertIsDisplayed()
            .performTextInput(password1And2)

        val registerBtn = composeRule.onNodeWithTag(TestTags.LoginRegisterScreen.REGISTER_BUTTON)
        registerBtn.assertExists()
            .assertIsDisplayed()
            .performClick()

        composeRule.waitForIdle()

        composeRule.waitUntil(5_000) {
            runBlocking {
                jwtStore.getPayload().first() != null
            }
        }

        val payload = jwtStore.getPayload().filterNotNull().first()
        Assert.assertEquals(expectedUserID, payload.sub)
        Assert.assertEquals(expectedUsername, payload.name)
        Assert.assertEquals(expectedEmail, payload.email)
        Assert.assertNotEquals("", payload.token)
    }

    @After
    fun teardown() = mockServer.shutdown()
}