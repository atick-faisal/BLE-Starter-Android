package dev.atick.ble.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.atick.ble.utils.BleUtils
import dev.atick.ble.utils.BleUtilsImpl
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class BleUtilsModule {
    @Binds
    @Singleton
    abstract fun bindBleUtils(
        bleUtilsImpl: BleUtilsImpl
    ): BleUtils
}