package com.dudar.mycoinapp

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.dudar.mycoinapp.viewmodels.GraphViewModel
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@RunWith(JUnit4::class)
class GraphViewModelTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var viewModel:GraphViewModel

    @Before
    fun setup() {
        viewModel = GraphViewModel()
    }

    @Test
    fun setTimespanTest(){
        val observer = mock() as Observer<String>
        this.viewModel.period.observeForever(observer)
        this.viewModel.setTimespan("1month")

        assertNotNull(this.viewModel.period.value)
        assertEquals("1month",this.viewModel.period.value)

        verify(observer).onChanged("1month")
    }

}