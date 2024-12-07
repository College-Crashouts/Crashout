package org.acjn.crashout

import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.google.maps.android.compose.GoogleMap

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()
@Composable
actual fun MapComponent() {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        val coordinates = LatLng(19.068857, 72.833)
        val markerState = rememberMarkerState(position = coordinates)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(coordinates, 10f)
        }
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = markerState,
                title = "Bandra West",
                snippet = "Mumbai"
            )
        }
    }
}

//package org.acjn.crashout
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.content.pm.PackageManager
//import android.os.Build
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.layout.Box
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.ui.platform.LocalContext
//import androidx.core.app.ActivityCompat
//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationServices
//import com.google.android.gms.maps.model.CameraPosition
//import com.google.android.gms.maps.model.LatLng
//import com.google.maps.android.compose.GoogleMap
//import com.google.maps.android.compose.Marker
//import com.google.maps.android.compose.rememberCameraPositionState
//import com.google.maps.android.compose.rememberMarkerState
//
//class AndroidPlatform : Platform {
//    override val name: String = "Android ${Build.VERSION.SDK_INT}"
//}
//
//actual fun getPlatform(): Platform = AndroidPlatform()
//
//@Composable
//@SuppressLint("MissingPermission")
//actual fun MapComponent() {
//    val context = LocalContext.current
//    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
//    var userLocation by remember { mutableStateOf<LatLng?>(null) }
//
//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission(),
//        onResult = { isGranted ->
//            print(isGranted)
//            if (isGranted) {
//                fetchUserLocation(fusedLocationClient) { location ->
//                    userLocation = location
//                    print(userLocation)
//                }
//            }
//        }
//    )
//
//    LaunchedEffect(Unit) {
//        if (ActivityCompat.checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            fetchUserLocation(fusedLocationClient) { location ->
//                userLocation = location
//            }
//        } else {
//            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//        }
//    }
//
//    Box(
//        modifier = Modifier.fillMaxSize(),
//    ) {
//        val defaultCoordinates = LatLng(19.068857, 72.833)
//        val markerState = rememberMarkerState(position = userLocation ?: defaultCoordinates)
//        val cameraPositionState = rememberCameraPositionState {
//            position = CameraPosition.fromLatLngZoom(userLocation ?: defaultCoordinates, 15f)
//        }
//
//        GoogleMap(
//            modifier = Modifier.fillMaxSize(),
//            cameraPositionState = cameraPositionState
//        ) {
//            userLocation?.let { location ->
//                Marker(
//                    state = markerState.apply { position = location },
//                    title = "Your Location",
//                    snippet = "This is your current location"
//                )
//            }
//        }
//    }
//}
//
//@SuppressLint("MissingPermission")
//fun fetchUserLocation(
//    fusedLocationClient: FusedLocationProviderClient,
//    onLocationUpdate: (LatLng) -> Unit
//) {
//    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//        if (location != null) {
//            onLocationUpdate(LatLng(location.latitude, location.longitude))
//        }
//    }
//}