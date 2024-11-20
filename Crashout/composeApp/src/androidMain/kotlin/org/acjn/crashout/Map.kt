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