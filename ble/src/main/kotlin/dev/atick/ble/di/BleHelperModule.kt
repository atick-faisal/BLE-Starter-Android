package dev.atick.ble.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.atick.ble.utils.BleHelper
import dev.atick.ble.utils.BleHelperImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BleHelperModule {

    @Binds
    @Singleton
    abstract fun bindBleHelper(bleHelperImpl: BleHelperImpl): BleHelper

}