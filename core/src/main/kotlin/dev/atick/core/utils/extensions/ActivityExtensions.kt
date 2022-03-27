package dev.atick.core.utils.extensions

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

fun ComponentActivity.launchForResult(
    intent: Intent,
    onSuccess: () -> Unit = {},
    onFailure: () -> Unit = {}

) {
    val resultCallback = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onSuccess.invoke()
        } else {
            onFailure.invoke()
        }
    }
    resultCallback.launch(intent)
}