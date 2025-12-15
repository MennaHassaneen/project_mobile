package com.example.iamjustgirl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.iamjustgirl.data.AppDatabase
import com.example.iamjustgirl.data.Product
import com.example.iamjustgirl.repository.ProductRepository
import com.example.iamjustgirl.ui.*
import com.example.iamjustgirl.viewmodel.ProductViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Makeup : Screen("makeup")
    object Perfume : Screen("perfume")
    object Favorites : Screen("favorites")
    object Cart : Screen("cart")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
}

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private lateinit var viewModel: ProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup database & repository
        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "products_db").build()
        val repository = ProductRepository(db.productDao())

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ProductViewModel(repository) as T
            }
        })[ProductViewModel::class.java]

        // Insert sample products
        lifecycleScope.launch {
            viewModel.insertSampleProducts(
                listOf(

                )
            )
        }

        setContent {
            val navController = rememberNavController()
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            var selectedProduct by remember { mutableStateOf<Product?>(null) }
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = currentBackStackEntry?.destination?.route ?: "welcome"

            MaterialTheme {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        CustomDrawer(
                            onItemClick = { route -> navController.navigate(route) },
                            onClose = { scope.launch { drawerState.close() } }
                        )
                    }
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize()
                    ) { paddingValues ->
                        Box(modifier = Modifier.padding(paddingValues)) {

                            // Navigation Host
                            NavHost(navController = navController, startDestination = "welcome") {
                                // الشاشات الأساسية
                                composable("welcome") { WelcomeScreen(navController) }
                                composable("signup") { SignupScreen(navController, viewModel) }
                                composable("login") { LoginScreen(navController, viewModel) }

                                // Home & Category Screens
                                composable("home") {
                                    HomeScreenWithDrawer(
                                        navController,
                                        viewModel,
                                        drawerScope = scope,
                                        drawerState = drawerState,
                                        onProductClick = { product ->
                                            selectedProduct = product
                                            scope.launch { sheetState.show() }
                                        }
                                    )
                                }
                                composable("makeup") {
                                    MakeupScreen(
                                        navController,
                                        viewModel,
                                        drawerScope = scope,
                                        drawerState = drawerState,
                                        onProductClick = { product ->
                                            selectedProduct = product
                                            scope.launch { sheetState.show() }
                                        }
                                    )
                                }
                                composable("perfume") {
                                    PerfumeScreen(
                                        navController,
                                        viewModel,
                                        drawerScope = scope,
                                        drawerState = drawerState,
                                        onProductClick = { product ->
                                            selectedProduct = product
                                            scope.launch { sheetState.show() }
                                        }
                                    )
                                }

                                // الشاشات الجديدة
                                composable("favorites") { FavoritesScreen(navController, viewModel) }
                                composable("cart") { CartScreen(navController, viewModel) }
                                composable("profile") { ProfileScreen(navController, scope, drawerState, viewModel) }
                                composable("edit_profile") { EditProfileScreen(navController, scope, drawerState, viewModel) }
                            }

                            // BottomSheet for product details
                            selectedProduct?.let { product ->
                                ModalBottomSheet(
                                    onDismissRequest = { selectedProduct = null },
                                    sheetState = sheetState
                                ) {
                                    ProductDetailsScreen(product = product, viewModel = viewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
