@file:Suppress("DEPRECATION")

package com.example.shoparoo.ui.homeScreen.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.shoparoo.R
import com.example.shoparoo.ui.theme.Purple40
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CouponsSliderWithIndicator(
    imageList: List<Int>,  // List of image resources
    slideDuration: Long = 3000L,  // Time duration between automatic slides
    couponText: String
) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val clipBoardManager: ClipboardManager = LocalContext.current.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    // Automatically scrolls through pages with a delay
    LaunchedEffect(pagerState) {
        coroutineScope.launch {
            while (true) {
                delay(slideDuration)
                val nextPage = (pagerState.currentPage + 1) % imageList.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Horizontal pager to display the images
        HorizontalPager(
            count = imageList.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(
                        color = Color(0xFFEFEEEE),
                        shape = RoundedCornerShape(28.dp)
                    )
            ) {
                Image(
                    painter = painterResource(id = imageList[page]),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                        .clip(RoundedCornerShape(28.dp))
                        .clickable {
                          if(imageList[page] == R.drawable.discount){
                              val clip = ClipData.newPlainText("Coupon Code", couponText)
                              clipBoardManager.setPrimaryClip(clip)
                              Toast.makeText(context, "Coupon Code Copied", Toast.LENGTH_SHORT).show()
                          }
                        },
                    contentScale = ContentScale.Crop
                )
            }
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp),
            activeColor = Purple40,
            inactiveColor = Color.Gray,
            indicatorWidth = 12.dp,
            spacing = 8.dp
        )
    }
}

/*
@Preview(showBackground = true)
@Composable
fun CouponsSliderWithIndicatorPreview() {
    CouponsSliderWithIndicator(
        imageList = listOf(
            R.drawable.ic_watch,
            R.drawable.fifty_off,
            R.drawable.nike_discount
        )
    )
}*/
