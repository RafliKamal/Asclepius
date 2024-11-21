package com.dicoding.asclepius.view.articles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.data.response.ArticlesItem
import com.dicoding.asclepius.data.response.CancerResponse
import com.dicoding.asclepius.retrofit.ApiClient
import kotlinx.coroutines.launch

class ArticlesViewModel : ViewModel() {

    private val _articles = MutableLiveData<List<ArticlesItem>>()
    val articles: LiveData<List<ArticlesItem>> get() = _articles

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchArticles(apiKey: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response: CancerResponse = ApiClient.apiService.getAllArticles(apiKey = apiKey)

                if (response.status == "ok") {
                    val filteredArticles = response.articles
                        ?.filterNotNull()
                        ?.filter { it.title != "[Removed]" }
                    _articles.value = filteredArticles ?: emptyList()
                    _errorMessage.value = ""
                } else {
                    _articles.value = emptyList()
                    _errorMessage.value = "Gagal memuat data"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _articles.value = emptyList()
                _errorMessage.value = "Tidak ada koneksi internet"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
