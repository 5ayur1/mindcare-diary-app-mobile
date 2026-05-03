package com.fiap.mindcarediary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class CreditosActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CreditosTela()
        }
    }
}

@Composable
fun CreditosTela() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFBBDEFB)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Topo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE91E63))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Créditos",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Card principal
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "MindCare Diary",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2196F3)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Desenvolvido por:",
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("• Erica Okamura")
                Text("• Gustavo Oliveira Silva")
                Text("• Michelle Sayuri Ueta")
                Text("• João Augusto Santiago Gomes")

                Spacer(modifier = Modifier.height(20.dp))

                Divider()

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Mês/Ano de desenvolvimento",
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Maio de 2026",
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Versão 1.0",
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
            }
        }
    }
}