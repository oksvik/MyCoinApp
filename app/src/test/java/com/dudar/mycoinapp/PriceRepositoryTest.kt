package com.dudar.mycoinapp

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.dudar.mycoinapp.model.ChartsResponse
import com.dudar.mycoinapp.model.PriceData
import com.dudar.mycoinapp.network.BlockchainApi
import com.dudar.mycoinapp.repository.PriceRepository
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


@RunWith(JUnit4::class)
class PriceRepositoryTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var repository: PriceRepository
    lateinit var api: BlockchainApi

    @Before
    fun setup() {
        api = mock()
        repository = PriceRepository(api)
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline()}
    }

    @Test
    fun getDataTest(){
        Mockito.`when`(api.getMarketPrice(ArgumentMatchers.anyString())).thenAnswer {
            return@thenAnswer Single.just(sampleResponse())
        }

        val observer = mock() as Observer<List<PriceData>>
        val testPriceLiveData = repository.getData(ArgumentMatchers.anyString())
        testPriceLiveData.observeForever(observer)

        Thread.sleep(1000)

        assertNotNull(testPriceLiveData.value)
        assertEquals(1,testPriceLiveData.value?.size )
    }

    private fun sampleResponse()= ChartsResponse("ok", "Market Price", "usd", "1months","average price", mutableListOf(PriceData(1546646400, 100.0)))
}