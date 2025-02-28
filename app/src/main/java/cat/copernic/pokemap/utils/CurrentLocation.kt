package cat.copernic.pokemap.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.compose.runtime.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

@Composable
fun rememberCurrentLocation(context: Context): Location? {
    var location by remember { mutableStateOf<Location?>(null) }

    LaunchedEffect(Unit) {
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        try {
            getLastKnownLocation(fusedLocationClient) { loc ->
                location = loc
            }
        } catch (e: SecurityException) {
            Log.e("Location", "No se tienen permisos para obtener la ubicación", e)
        }
    }

    return location
}

@SuppressLint("MissingPermission")
fun getLastKnownLocation(fusedLocationClient: FusedLocationProviderClient, onResult: (Location?) -> Unit) {
    val locationTask: Task<Location> = fusedLocationClient.lastLocation
    locationTask.addOnSuccessListener { location: Location? ->
        onResult(location)
    }
    locationTask.addOnFailureListener { e ->
        Log.e("Location", "Error obteniendo la ubicación", e)
        onResult(null)
    }
}
