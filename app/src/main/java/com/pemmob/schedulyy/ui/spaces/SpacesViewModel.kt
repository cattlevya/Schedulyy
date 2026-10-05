package com.pemmob.schedulyy.ui.spaces

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemmob.schedulyy.data.local.entity.SpaceEntity
import com.pemmob.schedulyy.domain.model.SpaceType
import com.pemmob.schedulyy.domain.repository.SpaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SpaceItemUiModel(
    val space: SpaceEntity,
    val subFolderCount: Int = 0,
    val prokerCount: Int = 0
)

data class CreateSpaceState(
    val isVisible: Boolean = false,
    val name: String = "",
    val type: SpaceType = SpaceType.ORGANISASI,
    val template: String = "HIMPUNAN", // HIMPUNAN, KEPANITIAAN, UKM, KOSONG
    val colorKey: String = "mint",
    val parentId: Long? = null,
    val errorMessage: String? = null
)

data class SpacesUiState(
    val currentParent: SpaceEntity? = null,
    val spaces: List<SpaceItemUiModel> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class SpacesViewModel @Inject constructor(
    private val spaceRepository: SpaceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpacesUiState(isLoading = true))
    val uiState: StateFlow<SpacesUiState> = _uiState.asStateFlow()

    private val _createSheetState = MutableStateFlow(CreateSpaceState())
    val createSheetState: StateFlow<CreateSpaceState> = _createSheetState.asStateFlow()

    private val _currentParentId = MutableStateFlow<Long?>(null)

    init {
        viewModelScope.launch {
            _currentParentId.collect { parentId ->
                loadSpaces(parentId)
            }
        }
    }

    private fun loadSpaces(parentId: Long?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val parent = if (parentId != null) spaceRepository.getSpaceById(parentId) else null

            val spaceFlow = if (parentId == null) {
                spaceRepository.getRootSpaces()
            } else {
                spaceRepository.getChildSpaces(parentId)
            }

            spaceFlow.collect { rawSpaces ->
                val uiSpaces = rawSpaces.map { space ->
                    val childCount = spaceRepository.getChildSpaces(space.id).firstOrNull()?.size ?: 0
                    val prokerCount = spaceRepository.getProkersBySpace(space.id).firstOrNull()?.size ?: 0
                    SpaceItemUiModel(
                        space = space,
                        subFolderCount = childCount,
                        prokerCount = prokerCount
                    )
                }

                _uiState.value = SpacesUiState(
                    currentParent = parent,
                    spaces = uiSpaces,
                    isLoading = false
                )
            }
        }
    }

    fun enterFolder(parentId: Long) {
        _currentParentId.value = parentId
    }

    fun navigateUp() {
        val current = _uiState.value.currentParent
        _currentParentId.value = current?.parentId
    }

    fun openCreateSheet(parentId: Long? = _currentParentId.value) {
        _createSheetState.value = CreateSpaceState(
            isVisible = true,
            parentId = parentId
        )
    }

    fun dismissCreateSheet() {
        _createSheetState.update { it.copy(isVisible = false) }
    }

    fun updateName(name: String) = _createSheetState.update { it.copy(name = name, errorMessage = null) }
    fun updateType(type: SpaceType) = _createSheetState.update { it.copy(type = type) }
    fun updateTemplate(template: String) = _createSheetState.update { it.copy(template = template) }
    fun updateColorKey(colorKey: String) = _createSheetState.update { it.copy(colorKey = colorKey) }

    fun saveSpace() {
        val state = _createSheetState.value
        val trimmed = state.name.trim()
        if (trimmed.isEmpty()) {
            _createSheetState.update { it.copy(errorMessage = "Nama folder tidak boleh kosong") }
            return
        }

        viewModelScope.launch {
            val space = SpaceEntity(
                parentId = state.parentId,
                type = state.type,
                name = trimmed,
                colorKey = state.colorKey,
                iconKey = "folder"
            )

            spaceRepository.insertSpaceWithTemplate(space, state.template)
            _createSheetState.value = CreateSpaceState(isVisible = false)
            loadSpaces(_currentParentId.value)
        }
    }
}
