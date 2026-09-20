package com.fiap.mindcarediary.paciente

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.fiap.mindcarediary.viewmodel.ChatViewModel

@Composable
fun HoldSpeechButton(model: ChatViewModel, enabled: Boolean, onHint: (String?) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var active by remember { mutableStateOf(false) }
    var stopping by remember { mutableStateOf(false) }
    val controller = remember(model, context) {
        HoldSpeechController(context) { text, error ->
            active = false; stopping = false
            model.finishSpeech(text, error)
        }
    }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        onHint(if (granted) "Microfone liberado. Segure o botão para falar." else "Permita o microfone nas configurações do aplicativo ou continue digitando.")
    }
    val start = {
        if (enabled && !active) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permission.launch(Manifest.permission.RECORD_AUDIO)
            } else if (model.beginSpeech()) {
                onHint(null); active = true; stopping = false
                controller.start()
            }
        }
    }
    val stop = { if (active && !stopping) { stopping = true; controller.release() } }
    val currentStart by rememberUpdatedState(start)
    val currentStop by rememberUpdatedState(stop)
    DisposableEffect(controller, lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) controller.interrupt()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer); controller.close() }
    }
    androidx.compose.foundation.layout.Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.size(48.dp)
                .background(if (active) Color(0xFFFAD0E8) else Color.Transparent, RoundedCornerShape(24.dp))
                .semantics(mergeDescendants = true) {
                    role = Role.Button
                    contentDescription = if (stopping) "Concluindo ditado" else if (active) "Microfone: ouvindo, solte para concluir" else "Microfone: segure para falar"
                    if (!enabled) disabled()
                    onClick(label = if (active) "Concluir ditado" else "Iniciar ditado") {
                        if (!enabled) false else { if (active) currentStop() else currentStart(); true }
                    }
                }
                .pointerInput(enabled) {
                    if (enabled) detectTapGestures(onPress = {
                        currentStart()
                        try { tryAwaitRelease() } finally { currentStop() }
                    })
                }.padding(12.dp)
        ) {
            val foreground = if (!enabled) Color.Gray else Color(0xFFF238A3)
            Icon(Icons.Default.Mic, contentDescription = null, tint = foreground, modifier = Modifier.size(24.dp))
        }
    }
}
