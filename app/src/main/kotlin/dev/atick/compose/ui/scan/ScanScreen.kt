package dev.atick.compose.ui.scan

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.atick.ble.data.ConnectionStatus

@Composable
@SuppressLint("MissingPermission")
fun ScanScreen(
    onScanResultClick: (String) -> Unit,
    navigateToDeviceFragment: () -> Unit,
    viewModel: ScanViewModel = viewModel()
) {
    val devices by viewModel.devices.collectAsState()
    val connectionStatus by viewModel.connectionStatus.collectAsState()
    var connectionInitiated by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LazyColumn {
            items(devices) { device ->
                Button(
                    onClick = {
                        onScanResultClick(device.address)
                        connectionInitiated = true
                    }
                ) {
                    Text(text = device.name)
                    Text(text = device.address)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        AnimatedVisibility(visible = connectionInitiated) {
            Text(text = connectionStatus.name)
            if (connectionStatus == ConnectionStatus.CONNECTED) {
                navigateToDeviceFragment.invoke()
            }
        }
    }
}