package dev.atick.ble.di

import android.content.Context
import com.welie.blessed.BluetoothCentralManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object BleCentralModule {

    @Provides
    @Singleton
    fun provideBleCentral(@ApplicationContext context: Context) =
        BluetoothCentralManager(context)
}