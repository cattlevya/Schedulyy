package com.pemmob.schedulyy.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemmob.schedulyy.domain.model.GeminiConnectionResult
import com.pemmob.schedulyy.domain.remote.GeminiClient
import com.pemmob.schedulyy.domain.repository.SettingsRepository
import com.pemmob.schedulyy.domain.repository.UserSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TestConnectionUiState {
    data object Idle : TestConnectionUiState
    data object Loading : TestConnectionUiState
    data class Success(val message: String) : TestConnectionUiState
    data class Error(val message: String) : TestConnectionUiState
}

data class SettingsUiState(
    val userSettings: UserSettings = UserSettings(),
    val inputApiKey: String = "",
    val isApiKeyVisible: Boolean = false,
    val testConnectionState: TestConnectionUiState = TestConnectionUiState.Idle,
    val availableModels: List<String> = listOf("gemini-1.5-flash", "gemini-2.0-flash", "gemini-2.5-flash"),
    val isCustomModelMode: Boolean = false,
    val customModelInput: String = "",
    val showClearDataDialog: Boolean = false,
    val infoMessage: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val geminiClient: GeminiClient,
    private val reminderScheduler: com.pemmob.schedulyy.data.notification.ReminderScheduler,
    private val dataBackupManager: com.pemmob.schedulyy.data.backup.DataBackupManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.getUserSettings().collect { settings ->
                _uiState.value = _uiState.value.copy(
                    userSettings = settings,
                    // Jika model yang disimpan tidak ada di list default, set custom mode
                    isCustomModelMode = settings.selectedModel !in _uiState.value.availableModels,
                    customModelInput = if (settings.selectedModel !in _uiState.value.availableModels) settings.selectedModel else ""
                )
            }
        }
    }

    fun onApiKeyInputChanged(text: String) {
        _uiState.value = _uiState.value.copy(
            inputApiKey = text,
            testConnectionState = TestConnectionUiState.Idle
        )
    }

    fun toggleApiKeyVisibility() {
        _uiState.value = _uiState.value.copy(
            isApiKeyVisible = !_uiState.value.isApiKeyVisible
        )
    }

    fun saveApiKey() {
        val key = _uiState.value.inputApiKey.trim()
        if (key.isBlank()) return
        viewModelScope.launch {
            settingsRepository.saveApiKey(key)
            _uiState.value = _uiState.value.copy(
                inputApiKey = "",
                isApiKeyVisible = false,
                testConnectionState = TestConnectionUiState.Idle,
                infoMessage = "API Key berhasil disimpan terenkripsi"
            )
        }
    }

    fun deleteApiKey() {
        viewModelScope.launch {
            settingsRepository.clearApiKey()
            _uiState.value = _uiState.value.copy(
                inputApiKey = "",
                testConnectionState = TestConnectionUiState.Idle,
                infoMessage = "API Key telah dihapus"
            )
        }
    }

    fun testConnection() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(testConnectionState = TestConnectionUiState.Loading)
            val key = if (_uiState.value.inputApiKey.isNotBlank()) {
                _uiState.value.inputApiKey.trim()
            } else {
                settingsRepository.getDecryptedApiKey()
            }

            if (key.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    testConnectionState = TestConnectionUiState.Error("API key belum dimasukkan")
                )
                return@launch
            }

            when (val result = geminiClient.testConnection(key)) {
                is GeminiConnectionResult.Success -> {
                    // Update daftar model yang berhasil didapatkan
                    val updatedModels = (result.availableModels + _uiState.value.availableModels).distinct()
                    _uiState.value = _uiState.value.copy(
                        testConnectionState = TestConnectionUiState.Success(result.message),
                        availableModels = updatedModels
                    )
                }
                is GeminiConnectionResult.InvalidKey -> {
                    _uiState.value = _uiState.value.copy(
                        testConnectionState = TestConnectionUiState.Error(result.message)
                    )
                }
                is GeminiConnectionResult.RateLimit -> {
                    _uiState.value = _uiState.value.copy(
                        testConnectionState = TestConnectionUiState.Error(result.message)
                    )
                }
                is GeminiConnectionResult.NetworkError -> {
                    _uiState.value = _uiState.value.copy(
                        testConnectionState = TestConnectionUiState.Error(result.message)
                    )
                }
                is GeminiConnectionResult.GeneralError -> {
                    _uiState.value = _uiState.value.copy(
                        testConnectionState = TestConnectionUiState.Error(result.message)
                    )
                }
            }
        }
    }

    fun selectModel(model: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCustomModelMode = false)
            settingsRepository.setSelectedModel(model)
        }
    }

    fun enableCustomModelMode() {
        _uiState.value = _uiState.value.copy(isCustomModelMode = true)
    }

    fun onCustomModelInputChanged(text: String) {
        _uiState.value = _uiState.value.copy(customModelInput = text)
    }

    fun saveCustomModel() {
        val model = _uiState.value.customModelInput.trim()
        if (model.isBlank()) return
        viewModelScope.launch {
            settingsRepository.setSelectedModel(model)
            _uiState.value = _uiState.value.copy(
                availableModels = (_uiState.value.availableModels + model).distinct(),
                infoMessage = "Model disetel ke $model"
            )
        }
    }

    fun setDefaultReminderMinutes(minutes: Int) {
        viewModelScope.launch {
            settingsRepository.setDefaultReminderMinutes(minutes)
            reminderScheduler.scheduleAll()
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setNotificationsEnabled(enabled)
            reminderScheduler.scheduleAll()
        }
    }

    fun updateProfile(
        name: String,
        nim: String,
        major: String,
        university: String,
        semester: String,
        colorKey: String
    ) {
        viewModelScope.launch {
            settingsRepository.updateProfile(name, nim, major, university, semester, colorKey)
            _uiState.value = _uiState.value.copy(infoMessage = "Profil berhasil disimpan")
        }
    }

    fun setOnboardingCompleted(completed: Boolean) {
        viewModelScope.launch {
            settingsRepository.setOnboardingCompleted(completed)
        }
    }

    fun setShowClearDataDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showClearDataDialog = show)
    }

    fun clearAllData() {
        viewModelScope.launch {
            settingsRepository.clearAllData()
            _uiState.value = _uiState.value.copy(
                showClearDataDialog = false,
                inputApiKey = "",
                testConnectionState = TestConnectionUiState.Idle,
                infoMessage = "Semua data telah dibersihkan"
            )
        }
    }

    suspend fun exportData(): String {
        return dataBackupManager.exportToJson()
    }

    fun importData(jsonString: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = dataBackupManager.restoreFromJson(jsonString)
            result.fold(
                onSuccess = { count ->
                    reminderScheduler.scheduleAll()
                    _uiState.value = _uiState.value.copy(infoMessage = "Berhasil memulihkan $count data")
                    onResult(true, "Berhasil memulihkan $count data")
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Format file cadangan tidak valid")
                }
            )
        }
    }

    fun clearInfoMessage() {
        _uiState.value = _uiState.value.copy(infoMessage = null)
    }
}
