package dev.atick.ble.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.atick.ble.repository.BleManager
import dev.atick.ble.repository.BleManagerImpl
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
@kotlinx.coroutines.ExperimentalCoroutinesApi
abstract class BleManagerModule {
    @Binds
    @Singleton
    abstract fun bindBLEManager(
        bleManagerImpl: BleManagerImpl
    ): BleManager
}