package com.example.amphibians.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.amphibians.AmphibiansApplication
import com.example.amphibians.data.AmphibiansRepository
import com.example.amphibians.network.AmphibiansPhoto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface AmphibiansUiState {
    data class Success(val photos: List<AmphibiansPhoto>) : AmphibiansUiState
    data object Error : AmphibiansUiState
    data object Loading : AmphibiansUiState
}

class AmphibiansViewModel(
    private val amphibiansRepository: AmphibiansRepository
) : ViewModel(){

    private val _amphibiansUiState = MutableStateFlow<AmphibiansUiState>(AmphibiansUiState.Loading)
    val amphibiansUiState = _amphibiansUiState.asStateFlow()

    init {
        getAmphibiansPhotos()
    }

    companion object{
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AmphibiansApplication)
                val amphibiansRepository = application.container.amphibiansRepository
                AmphibiansViewModel(amphibiansRepository = amphibiansRepository)
            }
        }
    }

    fun getAmphibiansPhotos() {
        viewModelScope.launch {
            _amphibiansUiState.value = try {
                val photos = amphibiansRepository.getAmphibiansPhotos()
                if (photos.isEmpty()) {
                    AmphibiansUiState.Error // Indent this line
                } else {
                    AmphibiansUiState.Success(photos) // Indent this line
                }
            } catch (e: IOException) {
                AmphibiansUiState.Error
            } catch (e: HttpException) {
                AmphibiansUiState.Error
            }
        }
    }
}