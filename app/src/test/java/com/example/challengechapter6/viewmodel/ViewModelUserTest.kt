package com.example.challengechapter6.viewmodel

import com.example.challengechapter6.model.EditUser
import com.example.challengechapter6.model.ResponseDataUserItem
import com.example.challengechapter6.model.User
import com.example.challengechapter6.network.RestfulAPIUser
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import retrofit2.Call

class ViewModelUserTest {

    lateinit var service : RestfulAPIUser

    @Before
    fun setUp() {
        service = mockk()
    }

    @Test
    fun getUsersTest() : Unit = runBlocking {
        val responseUsers = mockk<Call<List<ResponseDataUserItem>>>()
        every {
            runBlocking {
                service.getAllUser()
            }
        } returns responseUsers

        val result = service.getAllUser()

        verify {
            runBlocking { service.getAllUser() }
        }
        assertEquals(result, responseUsers)
    }

    @Test
    fun postDataTest(){
        val responseAddData = mockk<Call<ResponseDataUserItem>>()

        every {
            service.addUser(User("aghni", "qaghni", "abc@gmail.com", "sby", 20))
        }returns responseAddData

        val result = service.addUser(User("aghni", "qaghni", "abc@gmail.com", "sby", 20))

        verify {
            service.addUser(User("aghni", "qaghni", "abc@gmail.com", "sby", 20))
        }
        assertEquals(result, responseAddData)
    }

    @Test
    fun putDataTest(){
        val responseAddData = mockk<Call<List<ResponseDataUserItem>>>()

        every {
            service.putUser(1, EditUser("aghni", "qaghni", "abcd", 20, null))
        }returns responseAddData

        val result = service.putUser(1, EditUser("aghni", "qaghni", "abcd", 20, null))

        verify {
            service.putUser(1, EditUser("aghni", "qaghni", "abcd", 20, null))
        }
        assertEquals(result, responseAddData)
    }
}