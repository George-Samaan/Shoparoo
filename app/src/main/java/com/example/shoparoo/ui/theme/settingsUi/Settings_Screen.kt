package com.example.shoparoo.ui.theme.settingsUi

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.shoparoo.R


@SuppressLint("RememberReturnType")
@Composable
fun ProfileScreen(navController: NavController) {
    // State to control the visibility of the bottom sheet
    val showContactUsSheet = remember { mutableStateOf(false) }

    Modifier.background(Color.White)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        ProfileHeader()
        Spacer(modifier = Modifier.height(30.dp))
        ProfileOptions(navController, showContactUsSheet.value) {
            showContactUsSheet.value = !showContactUsSheet.value // Toggle bottom sheet
        }
        Spacer(modifier = Modifier.weight(1f))
        SignOutButton()

        // Show the contact us bottom sheet if clicked
        if (showContactUsSheet.value) {
            BottomSheetSetting(onDismiss = { showContactUsSheet.value = false }) {
                ContactUs() // Contact details
            }
        }
    }
}

@Composable
fun ProfileHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        Image(
            painter = painterResource(id = R.drawable.profile),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.user_name),
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.email),
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun ProfileOptions(navController: NavController, isContactUsSheetOpen: Boolean, onContactUsClick: () -> Unit) {
    Column {
        /*ProfileOptionItem(
            icon = painterResource(id = R.drawable.shopping),
            label = stringResource(R.string.my_orders),
            onClick = { navController.navigate("myOrders") }
        )*/

        /*ProfileOptionItem(
            icon = painterResource(id = R.drawable.heart),
            label = stringResource(R.string.my_wishlist),
            onClick = { navController.navigate("wishlist") }
        )*/

       /* ProfileOptionItem(
            icon = painterResource(id = R.drawable.location),
            label = stringResource(R.string.delivery_address),
            onClick = { navController.navigate("deliveryAddress") }
        )*/

        ProfileOptionItem(
            icon = painterResource(id = R.drawable.settings),
            label = stringResource(R.string.settings),
            onClick = { navController.navigate("settings") }
        )
        ProfileOptionItem(
            icon = painterResource(id = R.drawable.mobile),
            label = stringResource(R.string.contact_us),
            onClick = { onContactUsClick() },
            isSheetOpen = isContactUsSheetOpen
        )
    }
}


@Composable
fun ProfileOptionItem(icon: Painter, label: String, onClick: () -> Unit = {}, isSheetOpen: Boolean = false) {
    // Select the correct icon based on whether the bottom sheet is open or not
    val arrowIcon = if (isSheetOpen) R.drawable.ic_arrow_down else R.drawable.ic_arrow

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clip(RoundedCornerShape(12.dp))
            .shadow(10.dp, RoundedCornerShape(10.dp))
            .background(Color(0xFFF5F5F5))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(id = arrowIcon),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetSetting(onDismiss: () -> Unit, content: @Composable () -> Unit) {
    val modalBottomSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        content()
    }
}

@Composable
fun ContactUs() {
    val context = LocalContext.current
    val clipboardManager = remember { context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

    Column(modifier = Modifier.padding(10.dp)) {

        Row {
            Text(text = stringResource(R.string.contact_number_text), fontSize = 14.sp)
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = stringResource(R.string.contact_number),
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .clickable {
                        val clip = ClipData.newPlainText(
                            "Contact Number",
                            getString(context, R.string.contact_number)
                        )
                        clipboardManager.setPrimaryClip(clip)
                        Toast
                            .makeText(
                                context,
                                "Copied to clipboard!",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
            )
        }
        Row {
            Text(text = stringResource(R.string.email_text), fontSize = 14.sp)
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = stringResource(R.string.email_contact),
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .clickable {
                        val clip = ClipData.newPlainText(
                            "Email Contact",
                            getString(context, R.string.email_contact)
                        )
                        clipboardManager.setPrimaryClip(clip)
                        Toast
                            .makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT)
                            .show()
                    }
            )
        }
    }
}


@Composable
fun SignOutButton() {
    Text(
        text = stringResource(R.string.sign_out),
        color = Color(0xFFFF6D00),
        fontSize = 18.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(50.dp)
            .clickable { },
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold
    )
}


@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(navController = rememberNavController())
}
