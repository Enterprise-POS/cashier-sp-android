package com.pos.cashiersp.presentation.util

import android.Manifest
import android.util.Log
import android.webkit.PermissionRequest
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class WebViewPermissionHandler(
    private val launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>
) {
    var pendingRequest by mutableStateOf<PermissionRequest?>(null)
        private set

    fun handleRequest(request: PermissionRequest) {
        val isTrustedOrigin =
            request.origin.host == "www.trusted-domain.com" || request.origin.host == "app.local" // Always verify the origin before granting request


        if (!isTrustedOrigin) {
            Log.w(
                "WebViewPermission",
                "Blocked and denied permission request from untrusted origin: ${request.origin.host}"
            )
            request.deny()
            return
        }

        val androidPermissions = mutableListOf<String>()
        request.resources.forEach { resource ->
            when (resource) {
                PermissionRequest.RESOURCE_VIDEO_CAPTURE -> androidPermissions.add(Manifest.permission.CAMERA)
                PermissionRequest.RESOURCE_AUDIO_CAPTURE -> androidPermissions.add(Manifest.permission.RECORD_AUDIO)
            }
        }

        // Save the request and launch the Android system dialog
        pendingRequest = request
        launcher.launch(androidPermissions.toTypedArray())
    }

    fun onResult(results: Map<String, Boolean>) {
        val allGranted = results.values.all { it }
        Log.d("WebViewPermission", "Kotlin: All permissions granted? $allGranted")

        if (allGranted) {
            pendingRequest?.grant(arrayOf("/* list of permissions */"))
        } else {
            pendingRequest?.deny()
        }
        pendingRequest = null
    }
}
