package com.venom.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.data.BuildConfig
import com.venom.data.repo.UpdateChecker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UpdateState(
    val isForceUpdate: Boolean = false,
    val features: String = "",
    val latestVersionName: String = ""
)

@HiltViewModel
class UpdateViewModel @Inject constructor(
    private val updateChecker: UpdateChecker
) : ViewModel() {
    private val _state = MutableStateFlow(UpdateState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val config = updateChecker.checkForUpdates()
            _state.update {
                it.copy(
                    isForceUpdate = config.forceUpdateVersion > BuildConfig.VERSION_CODE.toInt(),
                    features = config.features,
                    latestVersionName = config.latestVersionName
                )
            }
        }
    }
}