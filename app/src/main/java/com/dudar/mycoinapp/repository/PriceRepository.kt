package com.dudar.mycoinapp.repository

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.dudar.mycoinapp.model.PriceData
import com.dudar.mycoinapp.network.BlockchainApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class PriceRepository {
    private val TAG = "PriceRepository"
    private lateinit var disposable: Disposable
    private val apiService  = BlockchainApi.create()

    fun getData(timespan:String) : MutableLiveData<List<PriceData>>{
        val marketPriceLiveData = MutableLiveData<List<PriceData>>()

        disposable = apiService.getMarketPrice(timespan)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { chartsResponse -> marketPriceLiveData.postValue(chartsResponse.values) },
                { err -> Log.e(TAG, "onError: ${err.message}")}
            )
        return marketPriceLiveData
    }

}