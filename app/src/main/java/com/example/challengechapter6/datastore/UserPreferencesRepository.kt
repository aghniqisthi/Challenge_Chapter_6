package com.example.challengechapter6.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.challengechapter6.UserProto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException

private val Context.userPreferencesStore: DataStore<UserProto> by dataStore(
    fileName = "userData",
    serializer = UserPreferencesSerializer
)

class UserPreferencesRepository(private val context: Context) {
    //create datastore proto
    //read datastore proto
    val readProto: Flow<UserProto> = context.userPreferencesStore.data.catch {
        // dataStore.data throws an IOException when an error is encountered when reading data
            exception -> if(exception is IOException){
        Log.e("tag", "Error reading sort order preferences", exception)
        emit(UserProto.getDefaultInstance())
    }
    else throw exception
    }

    //save data to datastore proto
    suspend fun saveData(id:Int, nama:String, username:String, password:String, address:String, age:Int){
        context.userPreferencesStore.updateData{
                preferences -> preferences.toBuilder().setId(id).build()
        }
        context.userPreferencesStore.updateData{
                preferences -> preferences.toBuilder().setName(nama).build()
        }
        context.userPreferencesStore.updateData{
                preferences -> preferences.toBuilder().setUsername(username).build()
        }
        context.userPreferencesStore.updateData{
                preferences -> preferences.toBuilder().setAge(age).build()
        }
        context.userPreferencesStore.updateData{
                preferences -> preferences.toBuilder().setPassword(password).build()
        }
        context.userPreferencesStore.updateData{
                preferences -> preferences.toBuilder().setAddress(address).build()
        }
    }

    //delete datastore proto
    suspend fun deleteData(){
        context.userPreferencesStore.updateData {
                preferences -> preferences.toBuilder().clearName().build()
        }
        context.userPreferencesStore.updateData {
                preferences -> preferences.toBuilder().clearUsername().build()
        }
        context.userPreferencesStore.updateData {
                preferences -> preferences.toBuilder().clearPassword().build()
        }
        context.userPreferencesStore.updateData {
                preferences -> preferences.toBuilder().clearAddress().build()
        }
        context.userPreferencesStore.updateData {
                preferences -> preferences.toBuilder().clearAge().build()
        }
    }
}