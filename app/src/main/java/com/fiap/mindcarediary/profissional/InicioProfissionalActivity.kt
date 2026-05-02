package com.fiap.mindcarediary.profissional

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fiap.mindcarediary.paciente.InicioPacienteTela

class InicioProfissionalctivity: ComponentActivity() {

    val email = intent.getStringExtra("email") ?: "Unknown"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

        }
    }
}