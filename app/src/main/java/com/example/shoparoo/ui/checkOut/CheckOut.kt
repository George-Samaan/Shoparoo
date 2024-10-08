package com.example.shoparoo.ui.checkOut

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.shoparoo.R
import com.example.shoparoo.ui.theme.primary
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.SupportMapFragment
import java.io.IOException
import java.util.Calendar
import java.util.Locale
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CheckoutScreen(navController: NavController) {
    var selectedPaymentMethod by remember { mutableStateOf("cash") }
    var showAddCreditCardScreen by remember { mutableStateOf(false) }

    var cardHolderName by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var expirationMonth by remember { mutableStateOf("") }
    var expirationYear by remember { mutableStateOf("") }

    val context = LocalContext.current

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp)
        ) {
            HeaderCheck(navController)
            Spacer(modifier = Modifier.height(16.dp))
            Location()
            Spacer(modifier = Modifier.height(16.dp))
            ChoosePaymentMethod(
                selectedPaymentMethod,
                onPaymentMethodSelected = { method ->
                    selectedPaymentMethod = method
                    showAddCreditCardScreen = method == "card"
                },
                showAddCreditCardScreen = showAddCreditCardScreen,
            )
            Spacer(modifier = Modifier.height(16.dp))
            CheckoutButtonCheck(
                selectedPaymentMethod = selectedPaymentMethod,
                cardHolderName = cardHolderName,
                cardNumber = cardNumber,
                expirationMonth = expirationMonth,
                expirationYear = expirationYear
            )
        }
    }
}

// Payment method selection component

@Composable
fun ChoosePaymentMethod(
    selectedPaymentMethod: String,
    onPaymentMethodSelected: (String) -> Unit,
    showAddCreditCardScreen: Boolean,

) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "Choose Payment Method",
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(Modifier.height(10.dp))

        // Cash on delivery option
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPaymentMethodSelected("cash") }
        ) {
            RadioButton(
                selected = selectedPaymentMethod == "cash",
                onClick = { onPaymentMethodSelected("cash") }
            )
            Image(
                painter = painterResource(id = R.drawable.cash),
                contentDescription = "Cash",
                modifier = Modifier.size(24.dp)
            )
            Text(
                "Cash on delivery",
                modifier = Modifier
                    .padding(start = 5.dp),
                fontSize = 14.sp
            )
        }

        Spacer(Modifier.height(20.dp))

        // Credit or Debit Card option
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPaymentMethodSelected("card") }
        ) {
            RadioButton(
                selected = selectedPaymentMethod == "card",
                onClick = { onPaymentMethodSelected("card") }
            )
            Image(
                painter = painterResource(id = R.drawable.card),
                contentDescription = "Card",
                modifier = Modifier.size(24.dp)
            )
            Text(
                "Credit or Debit Card",
                modifier = Modifier
                    .padding(start = 5.dp),
                fontSize = 14.sp
            )
        }

        // Show AddCreditCardScreen if "card" is selected
        if (showAddCreditCardScreen) {
            CreditCardItem()
        }
    }
}



// Header for the checkout screen
@Composable
fun HeaderCheck(navController: NavController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, start = 5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color(0xFFF5F5F5))
                .clickable { navController.popBackStack() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = stringResource(R.string.back),
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = stringResource(R.string.check_out),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .padding(end = 50.dp)
        )
    }
}

// _____________________________________________________________________________

// Location info for the checkout screen
@Composable
fun Location() {
    val context = LocalContext.current
    var locationText by remember { mutableStateOf("Fetching location...") }
    var showMap by remember { mutableStateOf(false) }

    // Request location permission
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
            Image(
                painter = painterResource(id = R.drawable.ic_location),
                contentDescription = stringResource(R.string.back),
                modifier = Modifier.size(24.dp)
            )
        }
        Column(modifier = Modifier.padding(start = 10.dp)) {
            Text(
                text = locationText,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
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

@SuppressLint("MissingPermission")
@Composable
fun LocationPickerMap(onLocationPicked: (String) -> Unit) {
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle()
    AndroidView(
        factory = { mapView },
        modifier = Modifier.fillMaxSize().fillMaxWidth()
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
        }
    }

}

// _____________________________________________________________________________

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


@Composable
fun CreditCardItem(
) {
    var selectedPaymentMethod by remember { mutableStateOf("card") } // Default to "cash" option
    val context = LocalContext.current
    var cardHolderName by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var expirationMonth by remember { mutableStateOf("") }
    var expirationYear by remember { mutableStateOf("") }

    var isCardHolderNameValid by remember { mutableStateOf(true) }
    var isCardNumberValid by remember { mutableStateOf(true) }
    var isExpirationMonthValid by remember { mutableStateOf(true) }
    var isExpirationYearValid by remember { mutableStateOf(true) }

    val currentYear =
        Calendar.getInstance().get(Calendar.YEAR) % 100 // get last two digits of the year

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Name on Card Field
        OutlinedTextField(
            value = cardHolderName,
            onValueChange = {
                cardHolderName = it
                isCardHolderNameValid = cardHolderName.isNotBlank()
            },
            label = { Text("Name on card") },
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = !isCardHolderNameValid
        )

        if (!isCardHolderNameValid) {
            Text(
                text = "Name on card cannot be empty",
                color = Color.Red,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card Number Field
        OutlinedTextField(value = cardNumber, onValueChange = {
            cardNumber = it
            isCardNumberValid = it.length == 16 && it.all { char -> char.isDigit() }
        },
            label = { Text("Card number") },
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = !isCardNumberValid,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        if (!isCardNumberValid) {
            Text(
                text = "Card number must be 16 digits",
                color = Color.Red,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Expiration Date Fields (MM/YY)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedTextField(
                value = expirationMonth,
                onValueChange = {
                    expirationMonth = it
                    isExpirationMonthValid = it.length == 2 && it.toIntOrNull() in 1..12
                },
                label = { Text("MM") },
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                singleLine = true,
                isError = !isExpirationMonthValid,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = expirationYear,
                onValueChange = {
                    expirationYear = it
                    isExpirationYearValid = it.length == 2 && it.toIntOrNull()
                        ?.let { year -> year >= currentYear } ?: false
                },
                label = { Text("YY") },
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                singleLine = true,
                isError = !isExpirationYearValid,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        if (!isExpirationMonthValid) {
            Text(
                text = "Invalid expiration month",
                color = Color.Red,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        if (!isExpirationYearValid) {
            Text(
                text = "Invalid expiration year",
                color = Color.Red,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))


        CheckoutButtonCheck(
            selectedPaymentMethod = selectedPaymentMethod,
            cardHolderName = cardHolderName, cardNumber = cardNumber,
            expirationMonth = expirationMonth, expirationYear = expirationYear
        )
    }
}


/*
@Composable
fun CheckoutButtonCheck(
    selectedPaymentMethod: String,
    cardHolderName: String,
    cardNumber: String,
    expirationMonth: String,
    expirationYear: String)
{
    val context = LocalContext.current


    Button(
        onClick = {
            when (selectedPaymentMethod) {
                "cash" -> {
                    Toast.makeText(context, "Cash on Delivery payment processed", Toast.LENGTH_SHORT).show()
                }
                "card" -> {
                    val isCardHolderNameValid = cardHolderName.isNotBlank()
                    val isCardNumberValid = cardNumber.length == 16 && cardNumber.all { it.isDigit() }
                    val isExpirationMonthValid = expirationMonth.length == 2 && expirationMonth.toIntOrNull() in 1..12
                    val isExpirationYearValid = expirationYear.length == 2 && expirationYear.toIntOrNull()?.let { it >= Calendar.getInstance().get(Calendar.YEAR) % 100 } ?: false

                    if (isCardHolderNameValid && isCardNumberValid && isExpirationMonthValid && isExpirationYearValid) {
                        Toast.makeText(context, "Card Payment processed", Toast.LENGTH_SHORT)
                            .show()
                        // payment process here
                    } else {
                        Toast.makeText(context, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(50.dp)
    ) {
        Text(text = "Place Order", fontWeight = FontWeight.Bold, color = Color.White)
    }
}
*/

@Composable
fun CheckoutButtonCheck(
    selectedPaymentMethod: String,
    cardHolderName: String,
    cardNumber: String,
    expirationMonth: String,
    expirationYear: String
) {
    val context = LocalContext.current

    Button(
        onClick = {
            if (selectedPaymentMethod == "cash") {
                // Handle cash on delivery
                Toast.makeText(context, "Order placed with Cash on Delivery", Toast.LENGTH_SHORT).show()
            } else if (selectedPaymentMethod == "card") {
                // Handle card payment validation
                if (validateCardDetails(cardHolderName, cardNumber, expirationMonth, expirationYear)) {
                    Toast.makeText(context, "Order placed with Credit/Debit Card", Toast.LENGTH_SHORT).show()
                    // Handle card payment here
                    // Take location with you.
                } else {
                    Toast.makeText(context, "Invalid Card Details", Toast.LENGTH_SHORT).show()
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(50.dp)
    ) {
        Text("Place Order", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}


fun validateCardDetails(
    cardHolderName: String,
    cardNumber: String,
    expirationMonth: String,
    expirationYear: String
): Boolean {
    return cardHolderName.isNotBlank() &&
            cardNumber.length == 16 && cardNumber.all { it.isDigit() } &&
            expirationMonth.length == 2 && expirationMonth.toIntOrNull() in 1..12 &&
            expirationYear.length == 2 && expirationYear.toIntOrNull() != null
}

@Preview(showBackground = true, device = "id:pixel_8_pro")
@Composable
fun PreviewCheckoutScreen() {
    CheckoutScreen(navController = NavController(LocalContext.current))
}


/*
// Payment method selection component
@Composable
fun ChoosePaymentMethod(selectedPaymentMethod: String, onPaymentMethodSelected: (String) -> Unit) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "Choose Payment Method",
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(Modifier.height(10.dp))

        // Cash on delivery option
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPaymentMethodSelected("cash") }
        ) {
            Image(
                painter = painterResource(id = R.drawable.cash),
                contentDescription = "Cash",
                modifier = Modifier.size(24.dp)
            )
            Text(
                "Cash on delivery",
                modifier = Modifier
                    .padding(start = 5.dp),
                fontSize = 14.sp
            )
        }

        Spacer(Modifier.height(20.dp))

        // Credit or Debit Card option
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPaymentMethodSelected("card") }
        ) {
            Image(
                painter = painterResource(id = R.drawable.card),
                contentDescription = "Card",
                modifier = Modifier.size(24.dp)
            )
            Text(
                "Credit or Debit Card",
                modifier = Modifier
                    .padding(start = 5.dp),
                fontSize = 14.sp
            )
            Spacer(Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "Add",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
            Log.i(TAG, "checkUser: user is authenticated+${firebaseAuth.currentUser!!.email}")
*/

// Add Card Button
/* Button(
     onClick = {
         isCardHolderNameValid = cardHolderName.isNotBlank()
         isCardNumberValid =
             cardNumber.length == 16 && cardNumber.all { char -> char.isDigit() }
         isExpirationMonthValid =
             expirationMonth.length == 2 && expirationMonth.toIntOrNull() in 1..12
         isExpirationYearValid = expirationYear.length == 2 && expirationYear.toIntOrNull()
             ?.let { it >= currentYear } ?: false

         if (isCardHolderNameValid && isCardNumberValid && isExpirationMonthValid && isExpirationYearValid) {
             Toast.makeText(context, "Card Added", Toast.LENGTH_SHORT).show()

             // Call onCardAdded() without changing the payment method
             onCardAdded()
         } else {
             Toast.makeText(
                 context,
                 "Please fill in all fields correctly",
                 Toast.LENGTH_SHORT
             ).show()
         }
     },
     modifier = Modifier
         .fillMaxWidth()
         .height(50.dp),
     shape = RoundedCornerShape(25.dp),
     colors = ButtonDefaults.buttonColors(primary)
 ) {
     Text(text = "Add Card", fontWeight = FontWeight.Bold, color = Color.White)
 }*/