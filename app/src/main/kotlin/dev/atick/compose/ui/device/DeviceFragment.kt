package dev.atick.compose.ui.device

import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.core.ui.BaseComposeFragment


@AndroidEntryPoint
class DeviceFragment: BaseComposeFragment() {

    @Composable
    override fun ComposeUi() {
        DeviceScreen()
    }

}