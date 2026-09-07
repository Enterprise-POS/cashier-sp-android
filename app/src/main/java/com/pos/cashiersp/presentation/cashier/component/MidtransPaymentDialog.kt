package com.pos.cashiersp.presentation.cashier.component

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog

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


@Composable
fun MidtransPaymentDialog(
    paymentUrl: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card {
            Column {
                Row {
                    Text("Payment")

                    IconButton(
                        onClick = {
                            // tutup WebView/Dialog
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close payment gateway webview"
                        )
                    }
                }

                MidtransWebView(
                    paymentUrl = paymentUrl
                )
            }
        }
    }
}
