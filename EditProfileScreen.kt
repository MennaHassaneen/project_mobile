package com.example.iamjustgirl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
fun EditProfileScreen(
    navController: NavHostController,
    drawerScope: CoroutineScope,
    drawerState: DrawerState,
    viewModel: ProductViewModel
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val db = FirebaseFirestore.getInstance()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Load current user data
    LaunchedEffect(userId) {
        if (userId.isNotBlank()) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { doc ->
                    name = doc.getString("name") ?: ""
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
        // Top Row
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
                Text("Edit Profile", fontSize = 20.sp, color = RoseGold)
            }
            IconButton(onClick = { drawerScope.launch { drawerState.open() } }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = RoseGold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Name TextField
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email TextField
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                isLoading = true
                // Update Firestore
                db.collection("users").document(userId)
                    .update("name", name)
                    .addOnCompleteListener {
                        isLoading = false
                        navController.popBackStack() // العودة للشاشة السابقة
                    }

                // Optionally update email in Firebase Auth
                val user = FirebaseAuth.getInstance().currentUser
                user?.updateEmail(email)?.addOnCompleteListener {
                    // Handle success/failure if needed
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = RoseGold),
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Saving..." else "Save", color = Color.White)
        }
    }
}
