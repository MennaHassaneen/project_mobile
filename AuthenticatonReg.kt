package com.example.iamjustgirl

interface AuthenticatonReg
{

    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit)

}