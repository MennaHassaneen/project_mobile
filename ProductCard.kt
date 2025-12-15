package com.example.iamjustgirl.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iamjustgirl.data.Product

private val RoseGold = Color(0xFFB76E79)
private val RoseGoldLight = Color(0xFFF8D7DA)
private val DarkText = Color(0xFF4A4A4A)

@Composable
fun ProductCard(
    product: Product,
    isFavorite: Boolean,
    inCart: Boolean,
    onFavoriteToggle: () -> Unit,
    onAddToCart: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White) // << هنا خلفية بيضاء
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        ) {
            // Image
            Image(
                painter = painterResource(id = product.image),
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Name & Price
            Text(
                text = product.name,
                fontSize = 16.sp,
                color = RoseGold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${product.price} EGP", color = DarkText.copy(alpha = 0.8f))

            Spacer(modifier = Modifier.weight(1f))

            // Buttons Row
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Favorite Button
                Button(
                    onClick = { onFavoriteToggle() },
                    contentPadding = PaddingValues(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFavorite) RoseGold else RoseGoldLight,
                        contentColor = if (isFavorite) Color.White else RoseGold
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (isFavorite) "❤️" else "♡")
                }

                // Cart Button
                Button(
                    onClick = { onAddToCart() },
                    contentPadding = PaddingValues(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (inCart) RoseGold else RoseGoldLight,
                        contentColor = if (inCart) Color.White else RoseGold
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (inCart) "✔" else "🛒")
                }
            }
        }
    }
}
