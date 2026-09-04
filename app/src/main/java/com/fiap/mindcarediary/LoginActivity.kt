package com.fiap.mindcarediary

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fiap.mindcarediary.paciente.InicioPacienteActivity
import com.fiap.mindcarediary.profissional.InicioProfissionalActivity
import com.fiap.mindcarediary.repository.AuthRepository
import com.fiap.mindcarediary.service.TokenManager
import com.fiap.mindcarediary.viewmodel.LoginViewModel
import com.fiap.mindcarediary.viewmodel.LoginViewModelFactory
import kotlin.math.log

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginTela(this)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun LoginTela(
    activity: LoginActivity = LoginActivity(),
    onGoogleClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
) {

    val context = LocalContext.current

    val tokenManager = remember {
        TokenManager(context.applicationContext)
    }

    val repository = remember {
        AuthRepository(
            tokenManager = tokenManager
        )
    }

    val loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(repository)
    )

    val tipoUsuario = activity.intent?.getStringExtra("tipoUsuario") ?: "Unknown"

    var nomeUsuario by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }


    val background = Color(0xFFDCEFFA)
    val darkBlue = Color(0xFF10104D)
    val pink = Color(0xFFE63B96)
    val blue = Color(0xFF1E88E5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.logo_mindcarediary),
            contentDescription = "Logo MindCare",
            modifier = Modifier
                .width(220.dp)
                .height(100.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Email",
            modifier = Modifier.fillMaxWidth(),
            color = darkBlue,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = nomeUsuario,
            onValueChange = { nomeUsuario = it },
            placeholder = { Text("name@example.com") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Senha",
            modifier = Modifier.fillMaxWidth(),
            color = darkBlue,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (showPassword)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(
                    onClick = { showPassword = !showPassword }
                ) {
                    Icon(
                        imageVector = if (showPassword)
                            Icons.Default.VisibilityOff
                        else
                            Icons.Default.Visibility,
                        contentDescription = "Mostrar senha"
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = {
                loginViewModel.generateFirebaseToken(nomeUsuario)
                loginViewModel.efetuarLogin(
                    nomeUsuario = nomeUsuario,
                    senha = senha,
                    onSuccess = { loginResponse ->

                        if (
                            "PACIENTE".equals(
                                loginResponse.userRole,
                                ignoreCase = true
                            )
                        ) {

                            val intent = Intent(
                                context,
                                InicioPacienteActivity::class.java
                            )

                            intent.putExtra(
                                "email",
                                nomeUsuario
                            )

                            context.startActivity(intent)

                        } else if (
                            "PROFISSIONAL".equals(
                                loginResponse.userRole,
                                ignoreCase = true
                            )
                        ) {

                            val intent = Intent(
                                context,
                                InicioProfissionalActivity::class.java
                            )

                            intent.putExtra(
                                "email",
                                nomeUsuario
                            )

                            context.startActivity(intent)
                        }
                    },
                    onError = { message ->
                        Toast.makeText(
                            context,
                            message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            },
            modifier = Modifier
                .width(250.dp)
                .height(52.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = pink
            )
        ) {
            Text(
                text = "Entrar",
                color = Color.White,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "ou",
            color = Color.Black,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onGoogleClick,
            modifier = Modifier
                .width(210.dp)
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = blue
            )
        ) {
            Text(
                text = "Google",
                color = Color.White,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Esqueceu a senha?",
            color = darkBlue,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 20.dp)
        )
    }
}

