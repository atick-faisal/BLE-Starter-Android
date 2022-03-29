package dev.atick.compose.ui.scan

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.core.ui.BaseComposeFragment

@AndroidEntryPoint
class ScanFragment: BaseComposeFragment() {

    private val viewModel: ScanViewModel by viewModels()

    @Composable
    override fun ComposeUi() {
        ScanScreen(
            ::onScanResultClick,
            ::navigateToDeviceFragment
        )
    }

    private fun navigateToDeviceFragment() {
        findNavController().navigate(
            ScanFragmentDirections.actionScanFragmentToDeviceFragment()
        )
    }

    private fun onScanResultClick(deviceAddress: String) {
        viewModel.connect(requireActivity(), deviceAddress)
    }

}