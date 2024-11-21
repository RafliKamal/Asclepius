package com.dicoding.asclepius.view.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.dicoding.asclepius.database.History
import com.dicoding.asclepius.repository.HistoryRepository

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: HistoryRepository = HistoryRepository(application)
    val allHistory: LiveData<List<History>> = repository.getAllHistory()

    fun insert(history: History) {
        repository.insert(history)
    }

    fun delete(history: History) {
        repository.delete(history)
    }
}
