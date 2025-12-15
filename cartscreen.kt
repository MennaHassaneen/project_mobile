package com.example.iamjustgirl.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.iamjustgirl.data.Product
import com.example.iamjustgirl.viewmodel.ProductViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    viewModel: ProductViewModel
) {
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var allProducts by remember { mutableStateOf(listOf<Product>()) }
    var cartMap by remember { mutableStateOf(mapOf<Int, Boolean>()) }

    // Load all products from ViewModel
    LaunchedEffect(Unit) {
        viewModel.getAllProducts { list -> allProducts = list }
    }

    // Load cart items from Firebase
    LaunchedEffect(userId, allProducts) {
        if (userId.isNotBlank() && allProducts.isNotEmpty()) {
            db.collection("users").document(userId)
                .addSnapshotListener { snapshot, _ ->
                    val cartList = snapshot?.get("card") as? List<Long> ?: emptyList()
                    cartMap = allProducts.associate { it.id to cartList.contains(it.id.toLong()) }
                }
        }
    }

    val cartProducts = allProducts.filter { cartMap[it.id] == true }
    val totalPrice = cartProducts.sumOf { it.price }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF0F5))
    ) {
        CenterAlignedTopAppBar(
            title = { Text("My Cart") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFFFF0F5))
        )

        if (cartProducts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Your cart is empty 🛒", style = MaterialTheme.typography.titleMedium)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cartProducts) { product ->
                    ProductCard(
                        product = product,
                        isFavorite = product.isFavorite,
                        inCart = true,
                        onFavoriteToggle = {},
                        onAddToCart = {
                            val userRef = db.collection("users").document(userId)
                            if (cartMap[product.id] == true)
                                userRef.update("card", com.google.firebase.firestore.FieldValue.arrayRemove(product.id))
                            else
                                userRef.update("card", com.google.firebase.firestore.FieldValue.arrayUnion(product.id))
                        },
                        onClick = {}
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total")
                        Text("$totalPrice EGP", style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            // Placeholder للـ Checkout
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Checkout")
                    }
                }
            }
        }
    }
}
