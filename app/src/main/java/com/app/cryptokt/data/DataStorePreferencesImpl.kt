package com.app.cryptokt.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.app.cryptokt.security.SecurityUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore(
    name = "app_user_preferences"
)
class DataStorePreferences(
    context: Context,
    val securityUtil: SecurityUtil,
    val gson: Gson
) {


    val bytesToStringSeperator = "|"
    val keyAlias = "appkey"
    val dataStore = context.dataStore
    val ivToStringSeparator= ":iv:"

    suspend fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T):
            Flow<T> = dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        val result = preferences[key] ?: defaultValue
        result
    }


    suspend fun <T> putPreference(key: Preferences.Key<T>, value: T) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun <T> putSecurePreference(key: Preferences.Key<String>, value: T) {
        dataStore.edit { preferences ->
            val serializedInput = gson.toJson(value)
            val (iv, secureByteArray) = securityUtil.encryptData(keyAlias, serializedInput)
            val secureString = iv.joinToString(bytesToStringSeperator) + ivToStringSeparator + secureByteArray.joinToString(bytesToStringSeperator)
            preferences[key] = secureString
        }
    }

    suspend inline fun <reified T> getSecurePreference(
        key: Preferences.Key<String>,
        defaultValue: T
    ):
            Flow<T> = dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        val secureString = preferences[key] ?: return@map defaultValue
        val (ivString, encryptedString) = secureString.split(ivToStringSeparator, limit = 2)
        val iv = ivString.split(bytesToStringSeperator).map { it.toByte() }.toByteArray()
        val encryptedData = encryptedString.split(bytesToStringSeperator).map { it.toByte() }.toByteArray()
        val decryptedValue = securityUtil.decryptData(keyAlias, iv, encryptedData)
        val type = object : TypeToken<T>() {}.type
        gson.fromJson(decryptedValue, type) as T

    }


    suspend fun <T> removePreference(key: Preferences.Key<T>) {
        dataStore.edit {
            it.remove(key)
        }
    }

    suspend fun clearAllPreference() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

}