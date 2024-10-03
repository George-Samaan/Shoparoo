package com.example.shoparoo.ui.theme.homeScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoparoo.R
import com.example.shoparoo.ui.theme.Purple40
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState

@Suppress("DEPRECATION")
@Composable
fun CouponsSliderWithIndicator() {
    val pagerState = rememberPagerState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // HorizontalPager for coupons
        HorizontalPager(
            count = 4, // Number of pages (you can change this)
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp) // Height of the card
        ) {
            // Coupon Card content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(
                        color = Purple40, // Adjust background color
                        shape = RoundedCornerShape(28.dp) // Rounded corners
                    )
                    .padding(start=16.dp,end=16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Discount text
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Get Winter Discount",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                        Text(
                            text = "20% Off",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = "For Children",
                            fontSize = 16.sp,
                            color = Color.White,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // Image of the child
                    Image(
                        painter = painterResource(id = R.drawable.ic_watch),
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .padding(start = 16.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        // Dots Indicator
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

@Preview(showBackground = true)
@Composable
fun CouponsSliderWithIndicatorPreview() {
    CouponsSliderWithIndicator()
}