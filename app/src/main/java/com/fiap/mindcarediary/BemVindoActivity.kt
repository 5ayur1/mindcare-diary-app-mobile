package com.fiap.mindcarediary

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class BemVindoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BemVindoTela()
        }
    }
}

@Composable
@Preview(showBackground = true)
fun BemVindoTela() {

    val backgroundColor = Color(0xFFD7ECFA)
    val textColor = Color(0xFF0A0A4A)
    val blue = Color(0xFF1E88E5)
    val pink = Color(0xFFE63B96)

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Bem Vindo!",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )

        Spacer(modifier = Modifier.height(24.dp))

        Image(
            painter = painterResource(id = R.drawable.logo_mindcarediary),
            contentDescription = "Logo MindCare",
            modifier = Modifier.size(220.dp)
        )

        Button(
            onClick = {
                val intent = Intent(context, TipoUsuarioActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .width(210.dp)
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = blue
            )
        ) {
            Text(
                text = "Entrar",
                color = Color.White,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                val intent = Intent(context, CreditosActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .width(210.dp)
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = pink
            )
        ) {
            Text(
                text = "Créditos",
                color = Color.White,
                fontSize = 18.sp
            )
        }
    }
}