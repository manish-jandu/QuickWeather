package com.manishjandu.quickweather.utils

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtil {
    private const val TAG = "PermissionUtil"

    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(activity: Activity, permission: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, permission, requestCode)
    }
}