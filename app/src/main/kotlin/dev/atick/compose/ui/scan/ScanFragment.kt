package dev.atick.compose.ui.scan

import androidx.compose.runtime.Composable
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.core.ui.BaseComposeFragment

@AndroidEntryPoint
class ScanFragment: BaseComposeFragment() {

    @Composable
    override fun ComposeUi() {
        ScanScreen(::navigateToDeviceFragment)
    }

    private fun navigateToDeviceFragment() {
        findNavController().navigate(
            ScanFragmentDirections.actionScanFragmentToDeviceFragment()
        )
    }

}