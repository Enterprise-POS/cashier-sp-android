package com.pos.cashiersp.presentation.select_tenant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pos.cashiersp.common.TestTags
import com.pos.cashiersp.presentation.ui.theme.Gray300
import com.pos.cashiersp.presentation.ui.theme.Gray600
import com.pos.cashiersp.presentation.ui.theme.Light
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.Secondary100
import com.pos.cashiersp.presentation.ui.theme.White

@Composable
fun SelectTenantScreen(navController: NavController) {
    Scaffold(
        modifier = Modifier
            .background(color = Secondary100)
    ) { innerPadding ->
        Box {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp)
            ) {
                Text(
                    "Select a tenant",
                    fontSize = 28.sp,
                    color = Secondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TestTags.SelectTenantScreen.SELECT_TENANT_TITLE)
                )
                Text(
                    "Choose tenant that you want to manage",
                    fontSize = 12.sp,
                    color = Gray600,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TestTags.SelectTenantScreen.SELECT_TENANT_TITLE_SUB_TITLE)
                )
                Spacer(Modifier.height(20.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = White
                    ),
                ) {
                    Column(
                        Modifier
                            .padding(horizontal = 14.dp, vertical = 18.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            "Your tenants",
                            fontSize = 16.sp,
                            color = Secondary,
                            fontWeight = FontWeight.W400,
                        )
                        Spacer(Modifier.height(10.dp))
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(1),
                            modifier = Modifier.heightIn(max = 250.dp)
                        ) {
                            items(3) { item ->
                                Button(
                                    shape = RoundedCornerShape(CornerSize(8.dp)),
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    colors = ButtonDefaults.buttonColors().copy(containerColor = Light),
                                    contentPadding = PaddingValues(8.dp),
                                    onClick = { },
                                ) {
                                    Card(
                                        colors = CardDefaults.cardColors().copy(
                                            containerColor = Secondary
                                        ),
                                        shape = RoundedCornerShape(6.dp),
                                    ) {
                                        Text(
                                            "SC",
                                            color = White,
                                            fontWeight = FontWeight.W500,
                                            modifier = Modifier
                                                .padding(8.dp)
                                        )
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Column(
                                        modifier = Modifier.fillMaxWidth(.8f)
                                    ) {
                                        Text(
                                            "Sunrise Coffee Bar",
                                            color = Secondary,
                                            fontWeight = FontWeight.W400,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                        Text("Staff", fontSize = 13.sp, color = Gray300, fontWeight = FontWeight.W400)
                                    }
                                    Text(
                                        "Current",
                                        textAlign = TextAlign.Center,
                                        fontSize = 10.sp,
                                        color = White,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Primary)
                                            .height(18.dp)
                                            .padding(horizontal = 8.dp)
                                            .width(IntrinsicSize.Min)
                                    )
                                }
                            }

                            // User could any time add tenant even there is an limit
                            item {
                                Button(
                                    shape = RoundedCornerShape(CornerSize(8.dp)),
                                    colors = ButtonDefaults.buttonColors().copy(containerColor = Light),
                                    contentPadding = PaddingValues(8.dp),
                                    modifier = Modifier
                                        .padding(vertical = 4.dp)
                                        .testTag(TestTags.SelectTenantScreen.ADD_TENANT_BUTTON),
                                    onClick = { },
                                ) {
                                    Card(
                                        colors = CardDefaults.cardColors().copy(
                                            containerColor = Secondary
                                        ),
                                        shape = RoundedCornerShape(6.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            tint = White,
                                            modifier = Modifier
                                                .padding(7.dp)
                                                .size(18.dp),
                                            contentDescription = "Add new tenant icon",
                                        )
                                    }
                                    Spacer(Modifier.width(10.dp))
                                    Column(
                                        modifier = Modifier.fillMaxWidth(.98f)
                                    ) {
                                        Text(
                                            "Add new tenant",
                                            color = Secondary,
                                            fontWeight = FontWeight.W400,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),

                ) {
                TextButton(
                    onClick = { },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Primary,
                        contentColor = White
                    ),
                    shape = RoundedCornerShape(corner = CornerSize(12.dp)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag(TestTags.SelectTenantScreen.CONTINUE_BUTTON)
                ) {
                    Text("Continue")
                }
                Spacer(Modifier.height(4.dp))
                TextButton(
                    onClick = {},
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Secondary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag(TestTags.SelectTenantScreen.SWITCH_ACCOUNT_BUTTON)
                ) {
                    Text("Switch account")
                }
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}