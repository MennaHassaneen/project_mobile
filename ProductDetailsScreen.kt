package com.example.iamjustgirl.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.iamjustgirl.data.Product
import com.example.iamjustgirl.viewmodel.ProductViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

private val RoseGold = Color(0xFFB76E79)
private val RoseGoldLight = Color(0xFFF8D7DA)
private val DarkText = Color(0xFF4A4A4A)

@Composable
fun ProductDetailsScreen(product: Product, viewModel: ProductViewModel) {
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var isFavorite by remember { mutableStateOf(false) }
    var inCart by remember { mutableStateOf(false) }

    // جلب الحالة من Firebase
    LaunchedEffect(product.id) {
        if (userId.isNotBlank()) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val favList = document.get("fav") as? List<Long> ?: emptyList()
                    val cartList = document.get("card") as? List<Long> ?: emptyList()
                    isFavorite = favList.contains(product.id.toLong())
                    inCart = cartList.contains(product.id.toLong())
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        val painter = runCatching { painterResource(id = product.image) }.getOrNull()
        if (painter != null) {
            Image(
                painter = painter,
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("Image not available", color = Color.DarkGray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = product.name, style = MaterialTheme.typography.titleLarge)
        Text(text = "${product.price} EGP", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = {
                    if (userId.isNotBlank()) {
                        val userRef = db.collection("users").document(userId)
                        if (isFavorite) userRef.update("fav", FieldValue.arrayRemove(product.id))
                        else userRef.update("fav", FieldValue.arrayUnion(product.id))
                        isFavorite = !isFavorite
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFavorite) RoseGold else RoseGoldLight,
                    contentColor = if (isFavorite) Color.White else RoseGold
                )
            ) {
                Icon(Icons.Default.Favorite, contentDescription = "Favorite")
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isFavorite) "Added" else "Add to favourite")
            }

            Button(
                onClick = {
                    if (userId.isNotBlank()) {
                        val userRef = db.collection("users").document(userId)
                        if (inCart) userRef.update("card", FieldValue.arrayRemove(product.id))
                        else userRef.update("card", FieldValue.arrayUnion(product.id))
                        inCart = !inCart
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (inCart) RoseGold else RoseGoldLight,
                    contentColor = if (inCart) Color.White else RoseGold
                )
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (inCart) "Added to cart" else "Add to cart")
            }
        }
    }
}
