package dev.atick.compose.ui.home

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.core.ui.BaseComposeFragment

@AndroidEntryPoint
class HomeFragment: BaseComposeFragment() {

    @Composable
    override fun ComposeUi() {
        MaterialTheme {
            HomeScreen(::navigateToScanFragment)
        }
    }

    private fun navigateToScanFragment() {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToScanFragment()
        )
    }

}