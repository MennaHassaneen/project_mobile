package com.example.iamjustgirl.models

data class CartItem(
    val productid: Int = 0,
    val productname: String = "",
    val price: Double = 0.0,
    val quantity:Int=0
)

data class UserData(
    val username: String = "",
    val email: String = "",
    val fav: List<String> = emptyList(),
    val card: List<CartItem> = emptyList()
)
