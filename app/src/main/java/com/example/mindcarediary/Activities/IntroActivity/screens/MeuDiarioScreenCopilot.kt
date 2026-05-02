package com.example.mindcarediary.Activities.IntroActivity.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindcarediary.R

@Composable
@Preview(showBackground = true)
fun MeuDiarioScreenCopilot() {
    var selectedMood by remember { mutableStateOf<String?>(null) }
    var pontosPositivos by remember { mutableStateOf("") }
    var dificuldades by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            BottomNavigationBar()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFEAF3F8))
        ) {
            // Topo
            TopSection()

            // Conteúdo
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier)

                // Humor
                CardSection {
                    Text(
                        "Como você se sentiu hoje?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1A1C1E)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val moods = listOf(
                            "Ótimo" to "😁",
                            "Bem" to "🙂",
                            "Neutro" to "😐",
                            "Mal" to "☹️",
                            "Péssimo" to "😭"
                        )
                        moods.forEach { (label, emoji) ->
                            MoodItem(
                                label = label,
                                emoji = emoji,
                                selected = selectedMood == label,
                                onClick = { selectedMood = label }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pontos positivos
                CardSection {
                    Text(
                        "Pontos Positivos do Dia",
                        color = Color(0xFF689F38),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = pontosPositivos,
                        onValueChange = { pontosPositivos = it },
                        placeholder = {
                            Text(
                                "O que aconteceu de bom hoje? Quais momentos te deixaram feliz?",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedBorderColor = Color(0xFF689F38)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dificuldades
                CardSection {
                    Text(
                        "Dificuldades e Desafios",
                        color = Color(0xFFE57373),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = dificuldades,
                        onValueChange = { dificuldades = it },
                        placeholder = {
                            Text(
                                "O que te incomodou hoje? Houve algum momento difícil?",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedBorderColor = Color(0xFFE57373)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                // Botão
                Button(
                    onClick = { /* ação de salvar */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                ) {
                    Text("Salvar Diário", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun TopSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(Color(0xFFEA99C2))
            .statusBarsPadding()
            .padding(top = 8.dp, bottom = 12.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Primeira Linha: Perfil, Logo, Sair
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Usando intro_pic como placeholder para foto de perfil
            Image(
                painter = painterResource(id = R.drawable.user_woman),
                contentDescription = "Perfil",
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape),
                contentScale = ContentScale.Crop
            )

            Image(
                painter = painterResource(id = R.drawable.logo_mindcarediary),
                contentDescription = "Logo MindCare",
                modifier = Modifier.height(35.dp),
                contentScale = ContentScale.Fit
            )

            IconButton(onClick = { /* logout */ }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Sair",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Segunda Linha: Back e Titulo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            IconButton(
                onClick = { /* voltar */ },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.Black
                )
            }

            Text(
                text = "Meu Diário",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF311B92),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun CardSection(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            content()
        }
    }
}

@Composable
fun MoodItem(
    label: String,
    emoji: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Text(
            text = emoji,
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Color(0xFFE91E63) else Color.Gray
        )
    }
}

@Composable
fun BottomNavigationBar() {
    NavigationBar(
        containerColor = Color(0xFFDBE7F9),
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Início") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Color(0xFFE91E63),
                unselectedTextColor = Color(0xFFE91E63)
            )
        )
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = { Icon(Icons.Default.Description, contentDescription = null) },
            label = { Text("Diário") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color(0xFFE91E63),
                indicatorColor = Color(0xFFE91E63)
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
            label = { Text("Relatório") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Color(0xFFE91E63),
                unselectedTextColor = Color(0xFFE91E63)
            )
        )
    }
}
