@file:Suppress("DEPRECATION")

package com.example.shoparoo.ui.checkOut

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.shoparoo.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.Locale

@Composable
fun Location() {
    val context = LocalContext.current
    var locationText by remember { mutableStateOf("Fetching location...") }
    var showMap by remember { mutableStateOf(false) }
    var showManualDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getLocation(context) { address ->
                locationText = address ?: "Unable to fetch address"
            }
        } else {
            locationText = "Location permission denied"
        }
    }

    // Check and request permission on first composition
    LaunchedEffect(Unit) {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            getLocation(context) { address ->
                locationText = address ?: "Unable to fetch address"
            }
        } else {
            permissionLauncher.launch(permission)
        }
    }

    // When location text is clicked, show map
    if (showMap) {
        Dialog(onDismissRequest = { showMap = false }) {
            LocationPickerMap { newAddress ->
                locationText = newAddress
                showMap = false
            }
        }
    }

    // Manual location entry dialog
    if (showManualDialog) {
        ManualLocationInputDialog(
            onConfirm = { manualAddress ->
                locationText = manualAddress
                showManualDialog = false
            },
            onDismiss = { showManualDialog = false }
        )
    }

    // Composable UI with clickable location text
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { showMap = true } // Show map on click
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "📍", fontSize = 28.sp)
            /*Image(
                painter = painterResource(id = R.drawable.ic_location),
                contentDescription = stringResource(R.string.back),
                modifier = Modifier.size(24.dp)
            )*/
        }
        Column(modifier = Modifier.padding(start = 10.dp)) {
            Text(
                text = locationText,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            // Edit manually button
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "Edit manually",
                color = Color.Blue,
                modifier = Modifier.clickable { showManualDialog = true }
            )
        }
    }
}

@Composable
fun ManualLocationInputDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var streetName by remember { mutableStateOf("") }
    var buildingNumber by remember { mutableStateOf("") }
    var floorNumber by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = { onDismiss() }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Enter Location Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                // Street Name Field
                OutlinedTextField(
                    value = streetName,
                    onValueChange = { streetName = it },
                    label = { Text("Street Name") },
                    isError = isError && streetName.isBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                if (isError && streetName.isBlank()) {
                    ErrorText("Street Name cannot be empty")
                }

                // Building Name/Number Field
                OutlinedTextField(
                    value = buildingNumber,
                    onValueChange = { buildingNumber = it },
                    label = { Text("Building Name/Number") },
                    isError = isError && buildingNumber.isBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                if (isError && buildingNumber.isBlank()) {
                    ErrorText("Building Name/Number cannot be empty")
                }

                // Floor/Villa Number Field
                OutlinedTextField(
                    value = floorNumber,
                    onValueChange = { floorNumber = it },
                    label = { Text("Floor/Villa Number") },
                    isError = isError && floorNumber.isBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                if (isError && floorNumber.isBlank()) {
                    ErrorText("Floor/Villa Number cannot be empty")
                }

                // Add and Cancel Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
                ) {
                    OutlinedButton(
                        onClick = { onDismiss() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (streetName.isNotBlank() && buildingNumber.isNotBlank() && floorNumber.isNotBlank()) {
                                val fullAddress = "$streetName, $buildingNumber, $floorNumber"
                                onConfirm(fullAddress)
                            } else {
                                isError = true
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}


fun getLocation(context: Context, onLocationResult: (String?) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    try {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses: List<Address>?

                    try {
                        addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                        if (addresses.isNullOrEmpty()) {
                            onLocationResult(null)
                        } else {
                            val address = addresses[0]
                            val addressText = "${address.getAddressLine(0)}"
                            onLocationResult(addressText)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        onLocationResult(null)
                    }
                } ?: onLocationResult(null)
            }
            .addOnFailureListener {
                onLocationResult(null)
            }
    } catch (e: SecurityException) {
        onLocationResult(null)
    }
}

@Composable
fun SearchBar(searchQuery: String, onSearchQueryChange: (String) -> Unit, searchError: Boolean) {
    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text("Search here ") },
            isError = searchError,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .shadow(4.dp, RoundedCornerShape(10.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Red
            )
        )
        if (searchError) {
            Text(
                text = "Location not found",
                color = Color.Red,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun LocationPickerMap(onLocationPicked: (String) -> Unit) {
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var searchError by remember { mutableStateOf(false) }
    val geocoder = Geocoder(context, Locale.getDefault())

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize().fillMaxWidth()
        ) { mapView ->
            mapView.getMapAsync { googleMap ->
                googleMap.uiSettings.isZoomControlsEnabled = true
                googleMap.uiSettings.isMyLocationButtonEnabled = true

                googleMap.setOnMapLongClickListener { latLng ->
                    val addresses: List<Address>?

                    try {
                        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                        if (!addresses.isNullOrEmpty()) {
                            val address = addresses[0].getAddressLine(0)
                            googleMap.clear()
                            googleMap.addMarker(MarkerOptions().position(latLng).title(address))
                            onLocationPicked(address)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                if (searchQuery.isNotEmpty()) {
                    val addresses = geocoder.getFromLocationName(searchQuery, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val location = addresses[0]
                        val latLng = LatLng(location.latitude, location.longitude)
                        googleMap.clear()
                        googleMap.addMarker(MarkerOptions().position(latLng).title(location.getAddressLine(0)))
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                        searchError = false
                    } else {
                        searchError = true
                    }
                }
            }
        }

        // Search Bar
        SearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            searchError = searchError
        )

        Text(
            text = "Long press to choose location",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .background(Color.White.copy(alpha = 0.7f), shape = RoundedCornerShape(8.dp))
                .padding(8.dp),
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            fontSize = 16.sp
        )
    }
}

@Composable
fun ErrorText(message: String) {
    Text(
        text = message,
        color = Color.Red,
        fontSize = 12.sp,
        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
    )
}

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    DisposableEffect(Unit) {
        mapView.onCreate(null)
        mapView.onResume()

        onDispose {
            mapView.onPause()
            mapView.onDestroy()
        }
    }

    return mapView
}



//Text(text = "📍", fontSize = 24.sp)
/*
@SuppressLint("MissingPermission")
@Composable
fun LocationPickerMap(onLocationPicked: (String) -> Unit) {
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var searchError by remember { mutableStateOf(false) }
    val geocoder = Geocoder(context, Locale.getDefault())


    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        ) { mapView ->
            mapView.getMapAsync { googleMap ->
                googleMap.uiSettings.isZoomControlsEnabled = true
                googleMap.uiSettings.isMyLocationButtonEnabled = true

                googleMap.setOnMapLongClickListener { latLng ->
                    // Convert lat/lng to address
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses: List<Address>?

                    try {
                        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                        if (!addresses.isNullOrEmpty()) {
                            val address = addresses[0].getAddressLine(0)
                            googleMap.clear()
                            googleMap.addMarker(MarkerOptions().position(latLng).title(address))
                            onLocationPicked(address)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                if (searchQuery.isNotEmpty()){
                    val addresses = geocoder.getFromLocationName(searchQuery, 1)
                    if (!addresses.isNullOrEmpty()){
                        val location = addresses[0]
                        val latLng = LatLng(location.latitude, location.longitude)
                        googleMap.clear()
                        googleMap.addMarker(MarkerOptions().position(latLng).title(location.getAddressLine(0)))
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                        searchError = false
                    }else{
                        searchError = true
                    }
                }
            }
        }
        // Search Bar
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by name") },
                isError = searchError,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .shadow(4.dp, RoundedCornerShape(10.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    errorContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Red
                )
            )
            if (searchError) {
                Text(
                    text = "Location not found",
                    color = Color.Red,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }


        // Text overlay at the bottom center of the map
        Text(
            text = "Long press to choose location",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .background(Color.White.copy(alpha = 0.7f), shape = RoundedCornerShape(8.dp))
                .padding(8.dp),
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            fontSize = 16.sp
        )
    }
}

*/
