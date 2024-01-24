package bangkit.roy.storyappmaster.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import bangkit.roy.storyappmaster.model.userAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class UserPref private constructor(private val dataStorePref: DataStore<Preferences>) {

    companion object {
        @Volatile
        private var INSTANCEUser: UserPref? = null
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val LOGIN_KEY = booleanPreferencesKey("state")

        fun getInstance(dataStore: DataStore<Preferences>): UserPref {
            return INSTANCEUser ?: synchronized(this) {
                val instanceuser = UserPref(dataStore)
                INSTANCEUser = instanceuser
                instanceuser
            }
        }
    }

    fun getAuth(): Flow<userAuth> {
        return dataStorePref.data.map { preferences ->
            userAuth(
                preferences[TOKEN_KEY] ?: "",
                preferences[LOGIN_KEY] ?: false
            )
        }
    }

    suspend fun saveSession(userAuthentication: userAuth) {
        dataStorePref.edit { preferences ->
            preferences[TOKEN_KEY] = userAuthentication.token
            preferences[LOGIN_KEY] = userAuthentication.isLogin
        }
    }

    suspend fun logoutSession() {
        dataStorePref.edit { preferences ->
            preferences[LOGIN_KEY] = false
            preferences[TOKEN_KEY] = ""
        }
    }
}