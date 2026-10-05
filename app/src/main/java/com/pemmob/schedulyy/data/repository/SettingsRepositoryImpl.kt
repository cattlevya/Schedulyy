package com.pemmob.schedulyy.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pemmob.schedulyy.data.local.SchedulyyDatabase
import com.pemmob.schedulyy.data.security.CryptoEngine
import com.pemmob.schedulyy.domain.repository.SettingsRepository
import com.pemmob.schedulyy.domain.repository.UserSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "schedulyy_user_settings")

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val cryptoEngine: CryptoEngine,
    private val database: SchedulyyDatabase
) : SettingsRepository {

    private val dataStore = context.dataStore

    override fun getUserSettings(): Flow<UserSettings> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { prefs ->
                val encKey = prefs[KEY_ENCRYPTED_API_KEY].orEmpty()
                val maskedKey = prefs[KEY_MASKED_API_KEY].orEmpty()
                UserSettings(
                    hasApiKey = encKey.isNotBlank(),
                    maskedApiKey = maskedKey,
                    selectedModel = prefs[KEY_SELECTED_MODEL] ?: "gemini-1.5-flash",
                    defaultReminderMinutes = prefs[KEY_DEFAULT_REMINDER] ?: 60,
                    notificationsEnabled = prefs[KEY_NOTIF_ENABLED] ?: true,
                    studentName = prefs[KEY_STUDENT_NAME] ?: "Dimas Pratama",
                    nim = prefs[KEY_NIM] ?: "2310101049",
                    major = prefs[KEY_MAJOR] ?: "Informatika",
                    university = prefs[KEY_UNIVERSITY] ?: "Universitas Nusantara",
                    semesterName = prefs[KEY_SEMESTER_NAME] ?: "5",
                    cardColorKey = prefs[KEY_CARD_COLOR] ?: "pastelButter",
                    hasCompletedOnboarding = prefs[KEY_ONBOARDING_COMPLETED] ?: false
                )
            }
    }

    override suspend fun getDecryptedApiKey(): String {
        val prefs = dataStore.data.first()
        val encKey = prefs[KEY_ENCRYPTED_API_KEY].orEmpty()
        return if (encKey.isBlank()) "" else cryptoEngine.decrypt(encKey)
    }

    override suspend fun saveApiKey(apiKey: String) {
        val trimmed = apiKey.trim()
        if (trimmed.isEmpty()) {
            clearApiKey()
            return
        }
        val encKey = cryptoEngine.encrypt(trimmed)
        val masked = if (trimmed.length > 4) {
            "••••••••" + trimmed.takeLast(4)
        } else {
            "••••••••"
        }
        dataStore.edit { prefs ->
            prefs[KEY_ENCRYPTED_API_KEY] = encKey
            prefs[KEY_MASKED_API_KEY] = masked
        }
    }

    override suspend fun clearApiKey() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_ENCRYPTED_API_KEY)
            prefs.remove(KEY_MASKED_API_KEY)
        }
    }

    override suspend fun setSelectedModel(model: String) {
        dataStore.edit { prefs ->
            prefs[KEY_SELECTED_MODEL] = model
        }
    }

    override suspend fun setDefaultReminderMinutes(minutes: Int) {
        dataStore.edit { prefs ->
            prefs[KEY_DEFAULT_REMINDER] = minutes
        }
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_NOTIF_ENABLED] = enabled
        }
    }

    override suspend fun updateProfile(
        name: String,
        nim: String,
        major: String,
        university: String,
        semester: String,
        cardColorKey: String
    ) {
        dataStore.edit { prefs ->
            prefs[KEY_STUDENT_NAME] = name
            prefs[KEY_NIM] = nim
            prefs[KEY_MAJOR] = major
            prefs[KEY_UNIVERSITY] = university
            prefs[KEY_SEMESTER_NAME] = semester
            prefs[KEY_CARD_COLOR] = cardColorKey
        }
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_ONBOARDING_COMPLETED] = completed
        }
    }

    override suspend fun clearAllData() {
        database.clearAllTables()
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    companion object {
        private val KEY_ENCRYPTED_API_KEY = stringPreferencesKey("gemini_api_key_enc")
        private val KEY_MASKED_API_KEY = stringPreferencesKey("gemini_api_key_masked")
        private val KEY_SELECTED_MODEL = stringPreferencesKey("gemini_selected_model")
        private val KEY_DEFAULT_REMINDER = intPreferencesKey("default_reminder_minutes")
        private val KEY_NOTIF_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val KEY_STUDENT_NAME = stringPreferencesKey("profile_student_name")
        private val KEY_NIM = stringPreferencesKey("profile_nim")
        private val KEY_MAJOR = stringPreferencesKey("profile_major")
        private val KEY_UNIVERSITY = stringPreferencesKey("profile_university")
        private val KEY_SEMESTER_NAME = stringPreferencesKey("profile_semester_name")
        private val KEY_CARD_COLOR = stringPreferencesKey("profile_card_color")
        private val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }
}
