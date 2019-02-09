package com.dudar.mycoinapp.di

import com.dudar.mycoinapp.network.BlockchainApi
import com.dudar.mycoinapp.repository.PriceRepository
import dagger.Module
import dagger.Provides

@Module
class BaseModule {

    @Provides
    fun provideBlockchainApi():BlockchainApi{ return BlockchainApi.create()}

    @Provides
    fun providePriceRepository(api: BlockchainApi):PriceRepository{return PriceRepository(api)}
}