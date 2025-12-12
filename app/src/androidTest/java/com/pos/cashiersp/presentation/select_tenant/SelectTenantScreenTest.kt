package com.pos.cashiersp.presentation.select_tenant

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertAny
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.hasTextExactly
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
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
import com.pos.cashiersp.presentation.cashier.CashierScreen
import com.pos.cashiersp.presentation.global_component.CashierDrawer
import com.pos.cashiersp.presentation.login_register.LoginRegisterScreen
import com.pos.cashiersp.presentation.ui.theme.CashierSPTheme
import com.pos.cashiersp.presentation.util.JwtStore
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.json.JSONObject
import org.junit.After
import org.junit.Assert
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

    // Add a mutable list to track tenants
    private val mockTenants = mutableListOf<String>()
    private var nextTenantId = 3

    // Helper function to extract tenant name from request
    private fun extractTenantName(requestBody: String): String {
        return try {
            val json = JSONObject(requestBody)
            json.getString("name")
        } catch (e: Exception) {
            "Unknown Tenant"
        }
    }

    @Before
    fun setUp() = runBlocking {
        hiltRule.inject()

        jwtStore.clearToken()
        jwtStore.saveToken(
            token = "TEST_TOKEN",
            user = User("johndoe@gmail.com", 1, "John Doe", "2025-12-07T14:42:26.517672Z")
        )
        // Reset the mock tenants list DB
        mockTenants.add(
            """
        {
            "id": 1,
            "name": "Test User Groups",
            "owner_user_id": 1,
            "is_active": true,
            "created_at": "2025-08-01T07:37:05.420491Z"
        }
        """.trimIndent()
        )
        mockTenants.add(
            """
        {
            "id": 2,
            "name": "Test User Groups 2",
            "owner_user_id": 1,
            "is_active": true,
            "created_at": "2025-08-02T07:28:05.349861Z"
        }
        """.trimIndent()
        )
        nextTenantId = 3

        mockServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = request.path ?: ""

                return when {
                    path == "/tenants/new" -> {
                        // Parse the request body to get the tenant name
                        val requestBody = request.body.readUtf8()
                        val tenantName = extractTenantName(requestBody)

                        // Add new tenant to the list
                        val newTenant = """
                            {
                                "id": ${nextTenantId++},
                                "name": "$tenantName",
                                "owner_user_id": 1,
                                "is_active": true,
                                "created_at": "2025-08-03T10:00:00.000000Z"
                            }
                        """.trimIndent()

                        mockTenants.add(newTenant)

                        MockResponse()
                            .setResponseCode(201)
                            .setBody("""Created""")
                    }

                    path.matches(Regex("^/tenants/\\d+$")) -> {
                        MockResponse()
                            .setResponseCode(200)
                            .setBody(
                                """
                                {
                                    "code": 200,
                                    "status": "success",
                                    "data":  {
                                        "requested_by": 165,
                                        "tenants": [
                                            ${mockTenants.joinToString(",")}
                                        ]
                                    }
                                }
                            """.trimIndent()
                            )
                    }

                    else -> {
                        println("Unmatched path: ${request.path}")
                        MockResponse().setResponseCode(404)
                    }
                }
            }
        }

        mockServer.start()

        composeRule.setContent {
            val navController = rememberNavController()
            CashierSPTheme {
                CashierDrawer(navController) { drawerState ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.SELECT_TENANT
                    ) {
                        composable(route = Screen.LOGIN_REGISTER) {
                            LoginRegisterScreen(navController)
                        }
                        composable(route = Screen.SELECT_TENANT) {
                            SelectTenantScreen(navController)
                        }
                        composable(route = Screen.CASHIER) {
                            CashierScreen(navController, drawerState)
                        }
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

    @Test
    fun openAddNewTenantDialog_createNewTenant() {
        composeRule.waitForIdle()

        // Wait for the ADD_TENANT_BUTTON to be displayed before proceeding
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithTag(TestTags.SelectTenantScreen.ADD_TENANT_BUTTON)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeRule.onNodeWithTag(TestTags.SelectTenantScreen.ADD_TENANT_BUTTON)
            .assertExists()
            .assertIsDisplayed()
            .performClick()

        composeRule.onNodeWithTag(TestTags.SelectTenantScreen.ADD_NEW_TENANT_DIALOG)
            .assertExists()
            .assertIsDisplayed()

        composeRule.onNodeWithTag(TestTags.SelectTenantScreen.TENANT_NAME_INPUT)
            .assertExists()
            .assertIsDisplayed()
            .performTextInput("John Doe Test Tenant")

        composeRule.onNodeWithTag(TestTags.SelectTenantScreen.CONFIRM_ADD_BUTTON)
            .assertExists()
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()

        composeRule.waitForIdle()

        // Wait for the new tenant card to be rendered
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule
                .onNodeWithText("John Doe Test Tenant")
                .assertExists()
                .isDisplayed()
        }
    }

    @Test
    fun afterSelectTenant_thenGoToCashierScreen() {
        composeRule.waitForIdle()

        // Verify 2 cards exist and click the first one,
        // using wait until because the test run too fast even the request not yet response may cause render error
        composeRule.waitUntil(5000) {
            composeRule.onAllNodesWithTag(TestTags.SelectTenantScreen.TENANT_CARD_BUTTON)
                .fetchSemanticsNodes().size == 2
        }
        composeRule.onAllNodesWithTag(TestTags.SelectTenantScreen.TENANT_CARD_BUTTON)[0].performClick()

        composeRule.waitForIdle()

        // Verify "Current" label appears
        composeRule.onAllNodesWithText("Current")
            .assertCountEquals(1)

        // Click continue button
        composeRule.onNodeWithTag(TestTags.SelectTenantScreen.CONTINUE_BUTTON)
            .assertExists()
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()

        composeRule.waitForIdle()

        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithTag(TestTags.CashierScreen.CASHIER_TITLE)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Now assert it's displayed
        composeRule.onNodeWithTag(TestTags.CashierScreen.CASHIER_TITLE)
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun userClickSwitchAccount_redirectToLoginRegisterScreen() = runTest {
        val switchAccountBtn = composeRule.onNodeWithTag(TestTags.SelectTenantScreen.SWITCH_ACCOUNT_BUTTON)
        switchAccountBtn
            .assertExists()
            .isDisplayed()
        switchAccountBtn.performClick()

        // Check if the user really redirect to Login Register
        composeRule.onNodeWithTag(TestTags.LoginRegisterScreen.LOGIN_REGISTER_TITLE)

        composeRule.waitForIdle()

        // Check if jwt is null
        val payload = jwtStore.getPayload().first()
        Assert.assertNull("Payload should be null", payload)
    }

    @After
    fun teardown() {
        mockServer.shutdown()
    }
}