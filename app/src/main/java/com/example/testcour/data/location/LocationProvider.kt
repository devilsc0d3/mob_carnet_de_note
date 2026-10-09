package com.example.testcour.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationProvider(private val context: Context) {

    private val client = LocationServices.getFusedLocationProviderClient(context)

    fun hasPermission(): Boolean =
        listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            .any { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }

    /** Renvoie la position actuelle, ou la dernière connue, ou null si indisponible. */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        if (!hasPermission()) return null
        val cts = CancellationTokenSource()
        val current = client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
            .awaitOrNull { cts.cancel() }
        return current ?: client.lastLocation.awaitOrNull()
    }

    private suspend fun <T> Task<T>.awaitOrNull(onCancel: () -> Unit = {}): T? =
        suspendCancellableCoroutine { cont ->
            addOnSuccessListener { cont.resume(it) }
            addOnFailureListener { cont.resume(null) }
            cont.invokeOnCancellation { onCancel() }
        }
}
