package com.pos.cashiersp.presentation.select_tenant.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pos.cashiersp.model.dto.Tenant
import com.pos.cashiersp.presentation.ui.theme.Gray300
import com.pos.cashiersp.presentation.ui.theme.Gray600
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.White
import kotlin.collections.forEach

@Composable
fun TenantListCard(
    isLoading: Boolean,
    tenantList: List<Tenant>,
    onAddTenant: () -> Unit,
    maxListHeight: Dp = 250.dp
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier
                .padding(horizontal = 16.dp, vertical = 20.dp)
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
                modifier = Modifier.heightIn(max = maxListHeight)
            ) {
                if (isLoading) {
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Primary, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.height(6.dp))
                            Text("Loading tenant...", color = Gray300)
                        }
                    }
                } else if (tenantList.count() > 0) {
                    tenantList.forEach { tenant -> item { TenantCard(tenant) } }
                    /*
                    item {
                        Button(
                            shape = RoundedCornerShape(CornerSize(8.dp)),
                            colors = ButtonDefaults.buttonColors().copy(containerColor = Light),
                            contentPadding = PaddingValues(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .testTag(TestTags.SelectTenantScreen.ADD_TENANT_BUTTON),
                            onClick = onAddTenant,
                        ) {
                            Card(
                                colors = CardDefaults.cardColors().copy(containerColor = Secondary),
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Add new tenant",
                                    color = Secondary,
                                    fontWeight = FontWeight.W400,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    "Create a new workspace",
                                    fontSize = 13.sp,
                                    color = Gray300,
                                    fontWeight = FontWeight.W400
                                )
                            }
                        }
                    }
                     */
                } else {
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp, horizontal = 12.dp)
                        ) {
                            Text(
                                text = "No tenants yet",
                                color = Secondary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "Create your tenant on the Enterprise POS website:",
                                color = Gray600,
                                fontSize = 13.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 19.sp
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "https://enterprisepos.vercel.app",
                                color = Primary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
