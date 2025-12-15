package com.example.iamjustgirl

class Register(private val AuthRepo: AuthenticatonReg)
{
    var errorMessage: String? = null

    fun login(email: String, password: String, onSuccess: () -> Unit) {


        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Please fill in all fields"
            return
        }
        AuthRepo.login(email, password) { success, error ->
            if (success) {
                onSuccess()
            } else {
                errorMessage = error

            }
        }
    }
}