package com.example.iamjustgirl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iamjustgirl.data.Product
import com.example.iamjustgirl.repository.ProductRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    // قائمة المنتجات
    private val _productsList = MutableStateFlow<List<Product>>(emptyList())
    val productsList: StateFlow<List<Product>> = _productsList

    // Favorites لكل مستخدم
    private val _favorites = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val favorites: StateFlow<Map<String, Boolean>> = _favorites

    // Cart محليًا
    private val _cart = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val cart: StateFlow<Map<String, Boolean>> = _cart

    init {
        loadProducts()
    }

    // تحميل كل المنتجات من Repository
    private fun loadProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            _productsList.value = repository.getAllProducts()
        }
    }

    // إضافة منتجات تجريبية
    fun insertSampleProducts(products: List<Product>) {
        viewModelScope.launch(Dispatchers.IO) {
            products.forEach { repository.addProduct(it) }
            _productsList.value = repository.getAllProducts()
        }
    }

    // الحصول على كل المنتجات
    fun getAllProducts(onResult: (List<Product>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            onResult(repository.getAllProducts())
        }
    }

    // الحصول على المنتجات حسب الصنف
    fun getProductsByCategory(category: String, onResult: (List<Product>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val allProducts = repository.getAllProducts()
            val filtered = allProducts.filter { it.category.equals(category, ignoreCase = true) }
            onResult(filtered)
        }
    }

    // البحث في المنتجات
    fun searchProducts(name: String, onResult: (List<Product>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            onResult(repository.searchProducts(name))
        }
    }

    // تحميل Favorites من Firebase لكل مستخدم
    fun loadFavorites(userId: String) {
        db.collection("favorites").document(userId).get()
            .addOnSuccessListener { document ->
                val favs = document.data?.mapKeys { it.key.toString() }?.mapValues { it.value as Boolean } ?: emptyMap()
                _favorites.value = favs
            }
    }

    // تبديل حالة Favorite وتحديث Firebase
    fun toggleFavorite(userId: String, productId: Any) {
        val key = productId.toString()
        val current = _favorites.value[key] ?: false
        val newState = !current

        _favorites.update { map -> map + (key to newState) }

        db.collection("favorites").document(userId)
            .update(key, newState)
            .addOnFailureListener {
                // لو فشل، ارجع الحالة القديمة
                _favorites.update { map -> map + (key to current) }
            }
    }

    // إدارة السلة محليًا
    fun toggleCart(productId: Any) {
        val key = productId.toString()
        val current = _cart.value[key] ?: false
        val newState = !current
        _cart.update { map -> map + (key to newState) }
    }
}
