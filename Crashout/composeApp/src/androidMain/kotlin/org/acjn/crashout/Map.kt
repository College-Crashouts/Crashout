//package org.acjn.crashout
//
//import android.os.Build
//import androidx.compose.foundation.layout.Box
//import androidx.compose.runtime.Composable
//import com.google.android.gms.maps.GoogleMap
//import com.google.android.gms.maps.model.CameraPosition
//import com.google.android.gms.maps.model.LatLng
//import com.google.maps.android.compose.Marker
//import com.google.maps.android.compose.rememberCameraPositionState
//import com.google.maps.android.compose.rememberMarkerState
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.ui.Modifier
//import com.google.maps.android.compose.GoogleMap
//
//class AndroidPlatform : Platform {
//    override val name: String = "Android ${Build.VERSION.SDK_INT}"
//}
//
//actual fun getPlatform(): Platform = AndroidPlatform()
//@Composable
//actual fun MapComponent() {
//    Box(
//        modifier = Modifier.fillMaxSize(),
//    ) {
//        val coordinates = LatLng(19.068857, 72.833)
//        val markerState = rememberMarkerState(position = coordinates)
//        val cameraPositionState = rememberCameraPositionState {
//            position = CameraPosition.fromLatLngZoom(coordinates, 10f)
//        }
//        GoogleMap(
//            modifier = Modifier.fillMaxSize(),
//            cameraPositionState = cameraPositionState
//        ) {
//            Marker(
//                state = markerState,
//                title = "Bandra West",
//                snippet = "Mumbai"
//            )
//        }
//    }
//}

package org.acjn.crashout

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.delay
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

@Composable
@SuppressLint("MissingPermission")
actual fun MapComponent() {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    val markerState = remember { mutableStateOf<LatLng?>(null) }
    var showMap by remember { mutableStateOf(false) }
    var showAccountInfo by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            println("Permissions granted: $isGranted")
            if (isGranted) {
                fetchActiveLocationUpdates(fusedLocationClient) { location ->
                    userLocation = location
                }
            }
        }
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(19.068857, 72.833), 15f) // Default position
    }

    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fetchActiveLocationUpdates(fusedLocationClient) { location ->
                userLocation = location
            }
        } else {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Update marker and camera position whenever userLocation changes
    LaunchedEffect(userLocation) {
        userLocation?.let { location ->
            cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)
            markerState.value = location
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            markerState.value?.let { location ->
                Marker(
                    state = rememberMarkerState(position = location),
                    title = "Your Location",
                    snippet = "This is your current location"
                )
            }
        }

        // Print the current coordinates
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 72.dp) // Adjust padding to place button above BottomAppBar
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            Button(
                onClick = {
                    userLocation?.let { location ->
                        println("Button clicked! Current location: ${location.latitude}, ${location.longitude}")
                    } ?: println("Button clicked! Location not available.")
                }
            ) {
                Text("Send Location")
            }
        }

        BottomAppBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            backgroundColor = MaterialTheme.colors.primary
        ) {
            Icon(
                Icons.Filled.Home, "Home",
                modifier = Modifier
                    .weight(1f)
                    .clickable { showMap = false; showAccountInfo = false })
            Icon(
                Icons.Filled.LocationOn, "Target",
                modifier = Modifier
                    .weight(1f)
                    .clickable { /* target navigation */ })

            Icon(
                Icons.Filled.Person, "Profile",
                modifier = Modifier
                    .weight(1f)
                    .clickable { showMap = false; showAccountInfo = true })
        }
    }
}

@SuppressLint("MissingPermission")
fun fetchActiveLocationUpdates(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationUpdate: (LatLng) -> Unit
) {
    val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        5000 // 5 seconds
    ).build()

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                onLocationUpdate(LatLng(location.latitude, location.longitude))
            }
        }
    }

    fusedLocationClient.requestLocationUpdates(
        locationRequest,
        locationCallback,
        Looper.getMainLooper()
    )

}

/**
 * Updates the markers list to remove old markers and add a new one.
 */
fun updateMarkers(markers: MutableList<LatLng>, newLocation: LatLng) {
    markers.clear() // Remove old markers
    markers.add(newLocation) // Add the new marker
}