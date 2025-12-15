package com.example.iamjustgirl.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.iamjustgirl.R
import com.example.iamjustgirl.data.Product
import com.example.iamjustgirl.viewmodel.ProductViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private val BackgroundColor = Color(0xFFFFF0F5)
private val RoseGold = Color(0xFFB76E79)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenWithDrawer(
    navController: NavHostController,
    viewModel: ProductViewModel,
    drawerScope: CoroutineScope,
    drawerState: DrawerState,
    onProductClick: (Product) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var products by remember { mutableStateOf(listOf<Product>()) }
    var favoritesMap by remember { mutableStateOf(mapOf<Int, Boolean>()) }
    var cartMap by remember { mutableStateOf(mapOf<Int, Boolean>()) }

    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val categories = listOf(
        "Makeup" to R.drawable.makeup_icon,
        "Skincare" to R.drawable.skincare,
        "Perfume" to R.drawable.perfume
    )

    // تحميل المنتجات
    LaunchedEffect(searchQuery) {
        viewModel.getAllProducts { list ->
            products = if (searchQuery.isEmpty()) list else list.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
            favoritesMap = products.associate { it.id to (favoritesMap[it.id] ?: false) }
            cartMap = products.associate { it.id to (cartMap[it.id] ?: false) }
        }
    }

    // جلب المفضلة والسلة من Firebase
    LaunchedEffect(userId, products) {
        if (userId.isNotBlank() && products.isNotEmpty()) {
            db.collection("users").document(userId)
                .addSnapshotListener { snapshot, _ ->
                    val favList = snapshot?.get("fav") as? List<Long> ?: emptyList()
                    val cartList = snapshot?.get("card") as? List<Long> ?: emptyList()
                    favoritesMap = products.associate { it.id to favList.contains(it.id.toLong()) }
                    cartMap = products.associate { it.id to cartList.contains(it.id.toLong()) }
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(16.dp)
    ) {
        // -------------------------------
        // Row أعلى الصفحة بدل TopAppBar
        // -------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // السهم للرجوع للخلف
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = RoseGold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Home", fontSize = 20.sp, color = RoseGold)
            }
            // زر Menu لفتح القايمة الجانبية
            IconButton(onClick = { drawerScope.launch { drawerState.open() } }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = RoseGold)
            }
        }

        // -------------------------------
        // Search Bar
        // -------------------------------
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search products...") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // -------------------------------
        // Banner
        // -------------------------------
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.promo_banner2),
                contentDescription = "Banner",
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // -------------------------------
        // Categories
        // -------------------------------
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(categories) { category ->
                val (name, iconRes) = category
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .size(width = 90.dp, height = 100.dp)
                        .clickable {
                            when (name) {
                                "Makeup" -> navController.navigate("makeup")
                                "Skincare" -> navController.navigate("skincare")
                                "Perfume" -> navController.navigate("perfume")
                            }
                        }
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = name,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(name, fontSize = 13.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // -------------------------------
        // Products Grid
        // -------------------------------
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(products) { product ->
                val isFavorite = favoritesMap[product.id] ?: false
                val inCart = cartMap[product.id] ?: false

                ProductCard(
                    product = product,
                    isFavorite = isFavorite,
                    inCart = inCart,
                    onFavoriteToggle = {
                        if (userId.isNotBlank()) {
                            val userRef = db.collection("users").document(userId)
                            if (isFavorite) userRef.update("fav", FieldValue.arrayRemove(product.id))
                            else userRef.update("fav", FieldValue.arrayUnion(product.id))
                            favoritesMap = favoritesMap.toMutableMap().apply { put(product.id, !isFavorite) }
                        }
                    },
                    onAddToCart = {
                        if (userId.isNotBlank()) {
                            val userRef = db.collection("users").document(userId)
                            if (inCart) userRef.update("card", FieldValue.arrayRemove(product.id))
                            else userRef.update("card", FieldValue.arrayUnion(product.id))
                            cartMap = cartMap.toMutableMap().apply { put(product.id, !inCart) }
                        }
                    },
                    onClick = { onProductClick(product) }
                )
            }
        }
    }
}
