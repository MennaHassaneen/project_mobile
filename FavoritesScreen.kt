package com.example.iamjustgirl.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun FavoritesScreen(
    navController: NavController,
    viewModel: ProductViewModel
) {
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var allProducts by remember { mutableStateOf(listOf<Product>()) }
    var favoritesMap by remember { mutableStateOf(mapOf<Int, Boolean>()) }

    // Load all products from ViewModel
    LaunchedEffect(Unit) {
        viewModel.getAllProducts { list -> allProducts = list }
    }

    // Load favorites from Firebase
    LaunchedEffect(userId, allProducts) {
        if (userId.isNotBlank() && allProducts.isNotEmpty()) {
            db.collection("users").document(userId)
                .addSnapshotListener { snapshot, _ ->
                    val favList = snapshot?.get("fav") as? List<Long> ?: emptyList()
                    favoritesMap = allProducts.associate { it.id to favList.contains(it.id.toLong()) }
                }
        }
    }

    val favoriteProducts = allProducts.filter { favoritesMap[it.id] == true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF0F5))
    ) {
        CenterAlignedTopAppBar(
            title = { Text("Favorites") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFFFF0F5))
        )

        if (favoriteProducts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No favorites yet 💔", style = MaterialTheme.typography.titleMedium)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favoriteProducts) { product ->
                    ProductCard(
                        product = product,
                        isFavorite = true,
                        inCart = false,
                        onFavoriteToggle = {
                            val userRef = db.collection("users").document(userId)
                            if (favoritesMap[product.id] == true)
                                userRef.update("fav", com.google.firebase.firestore.FieldValue.arrayRemove(product.id))
                            else
                                userRef.update("fav", com.google.firebase.firestore.FieldValue.arrayUnion(product.id))
                        },
                        onAddToCart = {},
                        onClick = {}
                    )
                }
            }
        }
    }
}
