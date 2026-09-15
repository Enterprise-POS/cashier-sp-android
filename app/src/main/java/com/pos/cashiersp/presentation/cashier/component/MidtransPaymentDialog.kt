package com.pos.cashiersp.presentation.cashier.component

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pos.cashiersp.presentation.ui.theme.Light
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.White

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MidtransWebView(
    paymentUrl: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.javaScriptCanOpenWindowsAutomatically = true
                settings.domStorageEnabled = true
                webViewClient = WebViewClient()
                loadUrl(paymentUrl)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MidtransPaymentDialog(
    paymentUrl: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Light
            ),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.95f)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Payment",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                HorizontalDivider()

                // WebView takes remaining space, so footer stays visible
                MidtransWebView(
                    paymentUrl = paymentUrl,
                    modifier = Modifier.weight(1f)
                )

                HorizontalDivider()

                // Bottom bar - compact Cancel action
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = White
                        )
                    ) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}