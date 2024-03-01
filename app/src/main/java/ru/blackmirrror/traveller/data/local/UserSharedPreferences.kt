package ru.blackmirrror.traveller.data.local

import android.content.Context
import android.content.SharedPreferences

class UserSharedPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var username: String?
        get() = sharedPreferences.getString(KEY_USERNAME, null)
        set(value) {
            sharedPreferences.edit().putString(KEY_USERNAME, value).apply()
        }

    var password: String?
        get() = sharedPreferences.getString(KEY_PASSWORD, null)
        set(value) {
            sharedPreferences.edit().putString(KEY_PASSWORD, value).apply()
        }

    var currentToken: String?
        get() = sharedPreferences.getString(KEY_USER_TOKEN, null)
        set(value) {
            sharedPreferences.edit().putString(KEY_USER_TOKEN, value).apply()
        }

    var isGuest: Boolean?
        get() = sharedPreferences.getBoolean(KEY_IS_GUEST, false)
        set(value) {
            sharedPreferences.edit().putBoolean(KEY_IS_GUEST, value ?: false).apply()
        }

    fun clearPreferences() {
        sharedPreferences.edit().remove(KEY_USERNAME).apply()
        sharedPreferences.edit().remove(KEY_PASSWORD).apply()
        sharedPreferences.edit().remove(KEY_USER_TOKEN).apply()
        sharedPreferences.edit().putBoolean(KEY_IS_GUEST, false).apply()
    }

    companion object {
        private const val PREFS_NAME = "userDataPrefs"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val KEY_USER_TOKEN = "user_token"
        private const val KEY_IS_GUEST = "is_guest"
    }
}