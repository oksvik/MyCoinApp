package com.dudar.mycoinapp.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.dudar.mycoinapp.di.DaggerBaseComponent
import com.dudar.mycoinapp.model.PriceData
import com.dudar.mycoinapp.repository.PriceRepository
import javax.inject.Inject

class GraphViewModel : ViewModel() {
    @Inject
    lateinit var repository: PriceRepository

    init {
        DaggerBaseComponent.create().inject(this)
    }

    val period = MutableLiveData<String>()
    val pricesLiveData: LiveData<List<PriceData>> = Transformations.switchMap(period) {
            period -> repository.getData(period)
    }

    fun setTimespan(newTimespan: String) {
        period.value = newTimespan
    }

    override fun onCleared() {
        super.onCleared()
        repository.dispose()
    }
}