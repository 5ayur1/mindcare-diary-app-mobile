package com.fiap.mindcarediary.paciente

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

class PdfViewerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pdfPath = intent.getStringExtra("pdfPath")

        if (pdfPath.isNullOrBlank()) {
            finish()
            return
        }

        val file = File(pdfPath)

        if (!isPdfFile(file)) {

            Log.e(
                "PDF_DEBUG",
                "Arquivo não é um PDF válido"
            )

            Toast.makeText(
                this,
                "O arquivo recebido não é um PDF válido.",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        Log.d("PDF_DEBUG", "path=${file.absolutePath}")
        Log.d("PDF_DEBUG", "exists=${file.exists()}")
        Log.d("PDF_DEBUG", "size=${file.length()}")

        setContent {
            PdfViewerScreen(
                pdfPath = pdfPath,
                onBack = {
                    finish()
                }
            )
        }
    }
}

fun isPdfFile(file: File): Boolean {
    if (!file.exists() || file.length() < 5) {
        return false
    }

    file.inputStream().use { input ->
        val header = ByteArray(5)
        val bytesRead = input.read(header)
        if (bytesRead != 5) {
            return false
        }

        return String(
            header,
            Charsets.US_ASCII
        ) == "%PDF-"
    }
}

@Composable
fun PdfViewerScreen(
    pdfPath: String,
    onBack: () -> Unit
) {
    val file = remember(pdfPath) {
        File(pdfPath)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var pdfRenderer by remember {
        mutableStateOf<PdfRenderer?>(null)
    }

    DisposableEffect(file) {

        var descriptor: ParcelFileDescriptor? = null
        var renderer: PdfRenderer? = null

        try {
            if (!file.exists()) {
                throw IllegalStateException(
                    "Arquivo não encontrado"
                )
            }

            if (file.length() == 0L) {
                throw IllegalStateException(
                    "Arquivo PDF vazio"
                )
            }

            descriptor = ParcelFileDescriptor.open(
                file,
                ParcelFileDescriptor.MODE_READ_ONLY
            )

            renderer = PdfRenderer(descriptor)

            pdfRenderer = renderer

        } catch (e: Exception) {

            Log.e(
                "PDF_DEBUG",
                "Erro ao abrir PDF",
                e
            )

            errorMessage = "Não foi possível abrir o PDF."
        }

        onDispose {

            try {
                renderer?.close()
            } catch (e: Exception) {
                Log.e(
                    "PDF_DEBUG",
                    "Erro ao fechar PdfRenderer",
                    e
                )
            }

            try {
                descriptor?.close()
            } catch (e: Exception) {
                Log.e(
                    "PDF_DEBUG",
                    "Erro ao fechar ParcelFileDescriptor",
                    e
                )
            }

            pdfRenderer = null
        }
    }

    if (errorMessage != null) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = errorMessage ?: "Erro ao abrir PDF"
            )
        }

        return
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = {
                    Log.d(
                        "PDF_DEBUG",
                        "========== VOLTAR =========="
                    )

                    onBack()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar"
                )
            }

            Text(
                text = "Receita",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        pdfRenderer?.let { renderer ->

            LazyColumn(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                items(renderer.pageCount) { pageIndex ->

                    PdfPage(
                        pdfRenderer = renderer,
                        pageIndex = pageIndex
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PdfPage(
    pdfRenderer: PdfRenderer,
    pageIndex: Int
) {

    val bitmap = remember(
        pdfRenderer,
        pageIndex
    ) {

        val page = pdfRenderer.openPage(pageIndex)

        try {

            val bitmap = Bitmap.createBitmap(
                page.width,
                page.height,
                Bitmap.Config.ARGB_8888
            )

            page.render(
                bitmap,
                null,
                null,
                PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
            )

            bitmap

        } finally {
            page.close()
        }
    }

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "Página ${pageIndex + 1}",
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    )
}