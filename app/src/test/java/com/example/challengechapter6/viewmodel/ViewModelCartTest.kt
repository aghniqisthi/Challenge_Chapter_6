package com.example.challengechapter6.viewmodel

import com.example.challengechapter6.model.Cart
import com.example.challengechapter6.model.ResponseDataCartItem
import com.example.challengechapter6.network.RestfulAPICart
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import retrofit2.Call

class ViewModelCartTest {

    lateinit var service : RestfulAPICart

    @Before
    fun setUp() {
        service = mockk()
    }

    @Test
    fun getCartTest() : Unit = runBlocking {
        val response = mockk<Call<List<ResponseDataCartItem>>>()
        every {
            runBlocking {
                service.getAllCart(71)
            }
        } returns response

        val result = service.getAllCart(71)

        verify {
            runBlocking { service.getAllCart(71) }
        }
        assertEquals(result, response)
    }

    @Test
    fun postCartTest() : Unit = runBlocking {
        val response = mockk<Call<ResponseDataCartItem>>()
        every {
            runBlocking {
                service.addCart(71, Cart("produk A", "300", "eyeliner", "httpd://www.google.com", "#ffffff"))
            }
        } returns response

        val result = service.addCart(71, Cart("produk A", "300", "eyeliner", "httpd://www.google.com", "#ffffff"))

        verify {
            runBlocking {
                service.addCart(71, Cart("produk A", "300", "eyeliner", "httpd://www.google.com", "#ffffff"))
            }
        }
        assertEquals(result, response)
    }

    @Test
    fun deleteCartTest() : Unit = runBlocking {
        val response = mockk<Call<ResponseDataCartItem>>()
        every {
            runBlocking {
                service.deleteCart(70, 10)
            }
        } returns response

        val result = service.deleteCart(70, 10)

        verify {
            runBlocking {
                service.deleteCart(70, 10)
            }
        }
        assertEquals(result, response)
    }
}