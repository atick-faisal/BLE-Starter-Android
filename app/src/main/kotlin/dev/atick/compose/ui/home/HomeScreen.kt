package dev.atick.compose.ui.home

import androidx.compose.material.Text
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.Composable

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    Text(text = "Hi, mom!")
}