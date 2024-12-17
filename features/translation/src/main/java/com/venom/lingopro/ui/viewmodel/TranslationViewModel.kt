//    private val connectivityManager =
//        application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//
//    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
//        override fun onAvailable(network: Network) {
//            updateUi { copy(isNetworkAvailable = true) }
//            // Retry last failed translation if exists
//            _uiState.value.userInput.takeIf { it.isNotBlank() }?.let {
//                viewModelScope.launch {
//                    translate(it, _uiState.value.sourceLanguage, _uiState.value.targetLanguage)
//                }
//            }
//        }
//
//        override fun onLost(network: Network) {
//            updateUi { copy(isNetworkAvailable = false) }
//        }
//    }
//
//    init {
//        startNetworkCallback()
//        observeTranslationInputs()
//    }
//
//    private fun observeTranslationInputs() {
//        viewModelScope.launch {
//            _uiState.map {
//                TranslationInputs(
//                    it.userInput, it.sourceLanguage, it.targetLanguage, it.isNetworkAvailable
//                )
//            }.debounce(300).distinctUntilChanged().filter { it.isValid() }.collect { inputs ->
//                translate(inputs.userInput, inputs.sourceLanguage, inputs.targetLanguage)
//            }
//        }
//    }
//
//    private fun startNetworkCallback() {
//        val networkRequest =
//            NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
//                .build()
//        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//        connectivityManager.unregisterNetworkCallback(networkCallback)
//    }
//
//
//
//
//    fun toggleBookmark() {
//        val currentState = _uiState.value
//        val currentTranslation = currentState.translationResult?.sentences?.firstOrNull() ?: return
//
//        viewModelScope.launch {
//            val originalText = currentTranslation.orig.postprocessText()
//            val translatedText = currentTranslation.trans.postprocessText()
//            try {
//                val updatedEntry = repository.toggleBookmark(
//                    TranslationEntry(
//                        sourceLanguage = currentState.sourceLanguage,
//                        targetLanguage = currentState.targetLanguage,
//                        originalText = originalText,
//                        translatedText = translatedText,
//                        isBookmarked = !currentState.isBookmarked
//                    )
//                )
//                _uiState.update { it.copy(isBookmarked = updatedEntry.isBookmarked) }
//            } catch (e: Exception) {
//                _uiState.update { it.copy(error = "Failed to update bookmark: ${e.message}") }
//            }
//        }
//    }
//
//    private fun checkBookmarkStatus(word: String) {
//        viewModelScope.launch {
//            val isBookmarked = repository.isBookmarked(word)
//            _uiState.update { it.copy(isBookmarked = isBookmarked) }
//        }
//    }
//
//    val availableLanguages: List<LanguageItem> = repository.getAvailableLanguages()
//}
