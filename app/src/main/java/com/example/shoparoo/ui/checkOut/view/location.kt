@file:Suppress("DEPRECATION")

package com.example.shoparoo.ui.checkOut.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.shoparoo.R
import com.example.shoparoo.model.ShippingAddress
import com.example.shoparoo.ui.shoppingCart.viewModel.ShoppingCartViewModel
import com.example.shoparoo.ui.theme.grey
import com.example.shoparoo.ui.theme.primary
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.Locale

@Composable
fun Location(viewModel: ShoppingCartViewModel, draftOrderId: Long) {
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
                val shippingAddress = ShippingAddress(address1 = locationText)
                viewModel.updateShippingAddress(draftOrderId, shippingAddress)
            }
        } else {
            locationText = "Location permission denied"
        }
    }

    // Check and request permission on first composition
    LaunchedEffect(Unit) {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLocation(context) { address ->
                locationText = address ?: context.getString(R.string.unable_to_fetch_address)
            }
        } else {
            permissionLauncher.launch(permission)
            viewModel.updateShippingAddress(draftOrderId, ShippingAddress(locationText))
        }
    }

    // When location text is clicked, show map
    if (showMap) {
        Dialog(onDismissRequest = { showMap = false }) {
            LocationPickerMap { newAddress ->
                locationText = newAddress
                viewModel.updateShippingAddress(draftOrderId, ShippingAddress(newAddress))
                showMap = false
            }
        }
    }

    // Manual location entry dialog
    if (showManualDialog) {
        ManualLocationInputDialog(
            onConfirm = { manualAddress ->
                locationText = manualAddress
                viewModel.updateShippingAddress(draftOrderId, ShippingAddress(manualAddress))
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
        }
        Column(modifier = Modifier.padding(start = 10.dp)) {
            Text(
                text = locationText,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
            // Edit manually button
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "Edit Location",
                color = Color.Blue,
                fontSize = 16.sp,
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
                    text = stringResource(R.string.enter_location_details),
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                // Street Name Field
                OutlinedTextField(
                    value = streetName,
                    onValueChange = { streetName = it },
                    label = { Text("Street Name") },
                    isError = isError && streetName.isBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(25.dp),
                )
                if (isError && streetName.isBlank()) {
                    ErrorText(stringResource(R.string.street_name_cannot_be_empty))
                }

                // Building Name/Number Field
                OutlinedTextField(
                    value = buildingNumber,
                    onValueChange = { buildingNumber = it },
                    label = { Text(stringResource(R.string.building_name_number)) },
                    isError = isError && buildingNumber.isBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(25.dp),
                )
                if (isError && buildingNumber.isBlank()) {
                    ErrorText(stringResource(R.string.building_name_number_cannot_be_empty))
                }

                // Floor/Villa Number Field
                OutlinedTextField(
                    value = floorNumber,
                    onValueChange = { floorNumber = it },
                    label = { Text(stringResource(R.string.floor_villa_number)) },
                    isError = isError && floorNumber.isBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(25.dp),

                    )
                if (isError && floorNumber.isBlank()) {
                    ErrorText(stringResource(R.string.floor_villa_number_cannot_be_empty))
                }

                // Add and Cancel Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                ) {
                    OutlinedButton(
                        onClick = { onDismiss() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(grey)
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
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(primary)
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
            label = { Text(stringResource(R.string.search_here)) },
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
                text = stringResource(R.string.location_not_found),
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
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
        ) { mapView ->
            mapView.getMapAsync { googleMap ->
                googleMap.uiSettings.isZoomControlsEnabled = true
                googleMap.uiSettings.isMyLocationButtonEnabled = true

                val egyptLatLng = com.google.android.gms.maps.model.LatLng(30.033333, 31.233334)
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(egyptLatLng, 6f)
                googleMap.animateCamera(cameraUpdate, 2000, null)

                // Short Click: Animate and zoom in
                googleMap.setOnMapClickListener { latLng ->
                    googleMap.clear()
                    googleMap.addMarker(MarkerOptions().position(latLng))

                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 11f)
                    googleMap.animateCamera(cameraUpdate, 2000, null)
                }

                googleMap.setOnMapLongClickListener { latLng ->
                    val addresses: List<Address>?

                    try {
                        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                        if (!addresses.isNullOrEmpty()) {
                            val address = addresses[0].getAddressLine(0)
                            googleMap.clear()
                            googleMap.addMarker(MarkerOptions().position(latLng).title(address))

                            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14f)
                            googleMap.animateCamera(cameraUpdate, 2000, null)
                            onLocationPicked(address)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                // Handle search functionality
                if (searchQuery.isNotEmpty()) {
                    val addresses = geocoder.getFromLocationName(searchQuery, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val location = addresses[0]
                        val latLng = com.google.android.gms.maps.model.LatLng(
                            location.latitude,
                            location.longitude
                        )
                        googleMap.clear()
                        googleMap.addMarker(
                            MarkerOptions().position(latLng).title(location.getAddressLine(0))
                        )

                        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12f)
                        googleMap.animateCamera(cameraUpdate, 2000, null)
                        searchError = false
                    } else {
                        searchError = true
                    }
                }
            }
        }

        // Search Bar UI
        SearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            searchError = searchError
        )

        Text(
            text = stringResource(R.string.long_press_to_choose_location),
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



