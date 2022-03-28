package dev.atick.ble.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.atick.ble.repository.BLEManager
import dev.atick.ble.repository.BLEManagerImpl
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class BleManagerModule {
    @Binds
    @Singleton
    abstract fun bindBLEManager(
        bleManagerImpl: BLEManagerImpl
    ): BLEManager
}