package com.app.cryptokt.viewmodels

import android.app.Application
import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.cryptokt.data.DataStorePreferences
import com.app.cryptokt.security.SecurityUtil
import com.google.gson.Gson
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val datastorePreference = DataStorePreferences(
        application.applicationContext,
        SecurityUtil(),
        Gson()
    )
    val TAG = "RAGHAV"


    fun storeData(text: String) {
        Log.d(TAG, "storeData:  invoke")
        viewModelScope.launch {
            val preferenceskey = stringPreferencesKey("text")
            val preferenceskey2= stringPreferencesKey("text2")
            datastorePreference.putSecurePreference(preferenceskey, "hello")
            datastorePreference.putSecurePreference(preferenceskey2, "resnet")
            Log.d(TAG, "storeData:  stored in memory")
        }
    }

    fun readData() {
        viewModelScope.launch {
            val preferenceskey = stringPreferencesKey("text")
            val preferenceskey2 = stringPreferencesKey("text2")
            val preferenceskey3 = stringPreferencesKey("name")

            Log.d(TAG, "readData: Retrieval success")

            // Combine all three collect calls into a single collect call
            combine(
                datastorePreference.getSecurePreference(preferenceskey, "default"),
                datastorePreference.getSecurePreference(preferenceskey2, "default"),
                datastorePreference.getSecurePreference(preferenceskey3, "default")
            ) { value1, value2, value3 ->
                // Handle the retrieved values
                Log.d("RAGHAV", "Value 1: $value1")
                Log.d("RAGHAV", "Value 2: $value2")
                Log.d("RAGHAV", "Value 3: $value3")
            }.collect()
        }
    }


}