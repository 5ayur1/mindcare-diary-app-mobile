package com.fiap.mindcarediary.paciente

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fiap.mindcarediary.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun MiaAvatar() {
    val resources = LocalContext.current.resources
    val bitmap by produceState<ImageBitmap?>(null) {
        value = withContext(Dispatchers.IO) {
            BitmapFactory.decodeResource(resources, R.drawable.mia_profile,
                BitmapFactory.Options().apply { inSampleSize = 4; inScaled = false })?.asImageBitmap()
        }
    }
    val image = bitmap
    if (image == null) Box(Modifier.size(52.dp))
    else Image(image, "Foto de perfil da MIA", Modifier.size(52.dp).clip(CircleShape), contentScale = ContentScale.Crop)
}
