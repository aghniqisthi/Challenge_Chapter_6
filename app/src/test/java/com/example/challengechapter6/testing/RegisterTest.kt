package com.example.challengechapter6.testing

import com.example.challengechapter6.view.RegisterActivity
import org.junit.After
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class RegisterTest {
    lateinit var register : Register

    @Before
    fun setUp() {
        register = Register()
    }

    @After
    fun tearDown() {
    }

    @Test
    fun empty_username() {
        val result = register.validateRegistrationInput("", "aghni", "abc_123", "abc_123", 20, "surabaya")
        assertEquals("username empty", result, true)
    }
    @Test
    fun empty_name() {
        val result = register.validateRegistrationInput("qaghni", "", "abc_123", "abc_123", 20, "surabaya")
        assertEquals("name empty", result, false)
    }
    @Test
    fun empty_password() {
        val result = register.validateRegistrationInput("qaghni", "aghni", "", "abc_123", 20, "surabaya")
        assertEquals("password empty", result, false)
    }
    @Test
    fun empty_confirmpassword() {
        val result = register.validateRegistrationInput("qaghni", "aghni", "abc_123", "", 20, "surabaya")
        assertEquals("confirmed password empty", result, false)
    }
    @Test
    fun empty_age() {
        val result = register.validateRegistrationInput("qaghni", "aghni", "abc_123", "abc_123", 0, "surabaya")
        assertEquals("age empty", result, false)
    }
    @Test
    fun empty_address() {
        val result = register.validateRegistrationInput("qaghni", "aghni", "abc_123", "abc_123", 20, "")
        assertEquals("address empty", result, false)
    }

    @Test
    fun password_different() {
        val result = register.validateRegistrationInput("qaghni", "aghni", "abc_123", "bcd_123", 20, "surabaya")
        assertEquals("password different", result, false)
    }

    @Test
    fun pass_less_than_6() {
        val result = register.validateRegistrationInput("qaghni", "aghni", "ab_12", "ab_12", 20, "surabaya")
        assertEquals("password < 6", result, false)
    }
    @Test
    fun pass_more_than_30() {
        val result = register.validateRegistrationInput("qaghni", "aghni", "qwertyuioplkjhgfdsamnbvcxz_12345", "qwertyuioplkjhgfdsamnbvcxz_12345", 20, "surabaya")
        assertEquals("password > 30", result, false)
    }
}