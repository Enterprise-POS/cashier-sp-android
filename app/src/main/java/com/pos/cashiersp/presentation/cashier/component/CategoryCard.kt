package com.pos.cashiersp.presentation.cashier.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pos.cashiersp.model.domain.Category
import com.pos.cashiersp.presentation.cashier.CashierEvent
import com.pos.cashiersp.presentation.cashier.CashierViewModel
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.White

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CategoryCard(category: Category, viewModel: CashierViewModel = hiltViewModel()) {
    val selectedCategoryId = viewModel.selectedCategory.value
    val active = selectedCategoryId == category.id
    Card(
        colors = CardDefaults.cardColors(containerColor = if (active) Primary else Secondary),
        modifier = Modifier.height(36.dp),
        shape = RoundedCornerShape(14.dp),
        onClick = { viewModel.onEvent(CashierEvent.OnSelectCategory(category.id)) }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                category.categoryName,
                style = TextStyle(color = White, fontWeight = FontWeight.W600, fontSize = 12.sp)
            )
        }
    }
}