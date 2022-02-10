package com.benjaminwan.ocr.ncnn.models

import androidx.lifecycle.*
import com.benjaminwan.ocr.ncnn.RecordsRepository
import kotlinx.coroutines.launch

class RecordsViewModel(private val repository: RecordsRepository) : ViewModel() {

    // Using LiveData and caching what allRecordss returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    var allRecords:  LiveData<List<Records>> = repository.selectAll().asLiveData()
    //var allRecords = MutableLiveData<List<Records>>().apply { value = repository.selectAll() }
    private var filterLiveData: MutableLiveData<List<Records>> = MutableLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(Records: Records) = viewModelScope.launch {
        repository.insert(Records)
        System.out.println("执行了Insert")
        System.out.println(Records.toString())
    }

    fun update(Records: Records) = viewModelScope.launch {
        repository.update(Records)
        System.out.println("执行了Update")
        System.out.println(Records.toString())
    }

    fun delete(Records: Records) = viewModelScope.launch {
        repository.delete(Records)
        System.out.println("执行了Delete")
        System.out.println(Records.toString())
    }

    fun searchByAddress(address: String) = viewModelScope.launch {
        allRecords = repository.searchByAddress(address).asLiveData()
        System.out.println("执行了searchByAddress")
    }

    fun searchByStatus(status: String) = viewModelScope.launch {
        allRecords = repository.searchByStatus(status).asLiveData()
        System.out.println("执行了searchByStatus")
    }
}

class RecordsViewModelFactory(private var repository: RecordsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecordsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecordsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}