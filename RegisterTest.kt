package com.example.iamjustgirl

import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class RegisterTest
{
    private lateinit var Repo:AuthenticatonReg
    private lateinit var Reg: Register

    @Before
    fun setUp() {
        Repo = mock()
        Reg = Register(Repo)
    }

    @Test
    fun `empty fields returns error`() {
        Reg.login("", "") {}
        assertEquals("Please fill in all fields", Reg.errorMessage)
    }

    @Test
    fun `login fails sets error message`() {

        whenever(Repo.login(any(), any(), any())).thenAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String?) -> Unit>(2)
            callback(false, "Wrong password")
        }

        Reg.login("test@gmail.com", "wrongpass") {}

        assertEquals("Wrong password", Reg.errorMessage)
    }

    @Test
    fun `login success calls onSuccess`() {
        var successCalled = false

        whenever(Repo.login(any(), any(), any())).thenAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String?) -> Unit>(2)
            callback(true, null)
        }

        Reg.login("user@gmail.com", "password") {
            successCalled = true
        }

        assertTrue(successCalled)
    }

}