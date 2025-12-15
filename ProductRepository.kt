package com.example.iamjustgirl.repository

import com.example.iamjustgirl.data.Product
import com.example.iamjustgirl.data.ProductDao

class ProductRepository(private val productDao: ProductDao) {
    suspend fun addProduct(product: Product) = productDao.insertProduct(product)
    suspend fun updateProduct(product: Product) = productDao.updateProduct(product)
    suspend fun deleteProduct(product: Product) = productDao.deleteProductById(product.id)
    suspend fun getAllProducts(): List<Product> = productDao.getAllProducts()
    suspend fun getProductsByCategory(category: String): List<Product> = productDao.getProductsByCategory(category)
    suspend fun searchProducts(name: String): List<Product> = productDao.searchProductsByName("%$name%")
}
