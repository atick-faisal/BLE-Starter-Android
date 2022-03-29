package dev.atick.compose.ui.scan

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
@SuppressLint("MissingPermission")
fun ScanScreen(
    viewModel: ScanViewModel = viewModel()
) {
    val context = LocalContext.current
    val devices by viewModel.devices.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LazyColumn {
            items(devices) { device ->
                Button(
                    onClick = {
                        viewModel.connect(context, device.address)
                    }
                ) {
                    Text(text = device.name)
                    Text(text = device.address)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}