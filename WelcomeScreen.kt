package com.example.iamjustgirl

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.iamjustgirl.R

private val RoseGold = Color(0xFFB76E79)
private val PinkChampagne = Color(0xFFF7D1D8)
private val LightGold = Color(0xFFFFE7CF)
private val OffWhite = Color(0xFFFFF8F6)

@Composable
fun WelcomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhite)

            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(18.dp))

        Image(
            painter = painterResource(id = R.drawable.logoo), // ضعي logo.png في res/drawable
            contentDescription = "Just Girl Logo",
            modifier = Modifier
                .size(300.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(18.dp))



        Text(
            text = "Just Girl",
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
            color = RoseGold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Beauty • Makeup • Skincare",
            fontSize = 15.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(Modifier.height(36.dp))

        Button(
            onClick = { navController.navigate("login") },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RoseGold)
        ) {
            Text("Sign In", color = Color.White, fontSize = 18.sp)
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = { navController.navigate("signup") },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Text("Create Account", color = RoseGold, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
