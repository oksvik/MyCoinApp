package com.dudar.mycoinapp.di

import com.dudar.mycoinapp.viewmodels.GraphViewModel
import dagger.Component

@Component(modules = [BaseModule::class])
interface BaseComponent {
    fun inject(viewModel: GraphViewModel)
}
