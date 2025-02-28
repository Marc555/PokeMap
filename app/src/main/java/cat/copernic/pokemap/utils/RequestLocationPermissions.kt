package cat.copernic.pokemap.utils

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat

fun requestLocationPermissions(activity: ComponentActivity) {
    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    if (permissions.any { ActivityCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED }) {
        ActivityCompat.requestPermissions(activity, permissions, 1)
    }
}
