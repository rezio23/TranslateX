package com.rezio23.translatex.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rezio23.translatex.network.TranslatorClient
import kotlinx.coroutines.launch

sealed class TranslateState {
    object Idle : TranslateState()
    object Loading : TranslateState()
    data class Success(val translatedText: String) : TranslateState()
    data class Error(val message: String) : TranslateState()
}

class TranslateViewModel : ViewModel() {

    private val _translateState = MutableLiveData<TranslateState>(TranslateState.Idle)
    val translateState: LiveData<TranslateState> = _translateState

    private val _sourceLang = MutableLiveData("auto")
    val sourceLang: LiveData<String> = _sourceLang

    private val _targetLang = MutableLiveData("es")
    val targetLang: LiveData<String> = _targetLang

    fun setSourceLang(code: String) { _sourceLang.value = code }
    fun setTargetLang(code: String) { _targetLang.value = code }

    fun swapLanguages(currentSourceText: String, currentTargetText: String): Pair<String, String> {
        val src = _sourceLang.value ?: "auto"
        val tgt = _targetLang.value ?: "en"
        // Don't swap if source is auto-detect
        if (src == "auto") return Pair(currentSourceText, currentTargetText)
        _sourceLang.value = tgt
        _targetLang.value = src
        return Pair(currentTargetText, currentSourceText)
    }

    fun translate(text: String) {
        if (text.isBlank()) {
            _translateState.value = TranslateState.Error("Please enter text to translate")
            return
        }
        val from = _sourceLang.value ?: "auto"
        val to = _targetLang.value ?: "en"

        if (from == to && from != "auto") {
            _translateState.value = TranslateState.Success(text)
            return
        }

        _translateState.value = TranslateState.Loading
        viewModelScope.launch {
            try {
                val result = TranslatorClient.translate(text, from, to)
                _translateState.value = TranslateState.Success(result)
            } catch (e: Exception) {
                _translateState.value = TranslateState.Error(e.message ?: "Translation failed")
            }
        }
    }
}
