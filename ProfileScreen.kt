package com.example.iamjustgirl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.iamjustgirl.viewmodel.ProductViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private val BackgroundColor = Color(0xFFFFF0F5)
private val RoseGold = Color(0xFFB76E79)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    drawerScope: CoroutineScope,
    drawerState: DrawerState,
    viewModel: ProductViewModel
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val db = FirebaseFirestore.getInstance()

    // Load user data from Firestore
    LaunchedEffect(userId) {
        if (userId.isNotBlank()) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    name = document.getString("name") ?: ""
                    email = FirebaseAuth.getInstance().currentUser?.email ?: ""
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(16.dp)
    ) {
        // Top Row with Back and Menu
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = RoseGold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("My Account", fontSize = 20.sp, color = RoseGold)
            }
            IconButton(onClick = { drawerScope.launch { drawerState.open() } }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = RoseGold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // User Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Name", color = RoseGold, fontSize = 14.sp)
                Text(name, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Email", color = RoseGold, fontSize = 14.sp)
                Text(email, fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Buttons for actions
        Button(
            onClick = {
                navController.navigate("edit_profile")
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = RoseGold)
        ) {
            Text("Edit Profile", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("welcome") {
                    popUpTo("welcome") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text("Log Out", color = Color.White)
        }
    }
}
