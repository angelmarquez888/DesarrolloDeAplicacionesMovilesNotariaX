package com.duoc.magicnotaria.ui.theme.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.duoc.magicnotaria.model.Destinos
import com.duoc.magicnotaria.ui.components.*
import com.duoc.magicnotaria.ui.theme.DarkBlue
import com.duoc.magicnotaria.ui.theme.GoldYellow
import com.duoc.magicnotaria.viewmodel.MainViewModel

@Composable
fun LoginScreen(navController: NavController, viewModel: MainViewModel) {
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding().imePadding().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(800)) + slideInVertically(tween(800)) { -it / 2 }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LogoMagicNotaria()
                Spacer(Modifier.height(16.dp))
                TituloMagicNotaria(colorMagic = DarkBlue, tamano = 40.sp)
                Text("Tu notaría en la palma de tu mano", fontSize = 13.sp, color = Color.Gray)
            }
        }
        Spacer(Modifier.height(32.dp))

        TarjetaBlanca {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Iniciar Sesión", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                CampoTexto(correo, { correo = it }, "Correo electrónico", teclado = KeyboardType.Email)
                CampoTexto(clave, { clave = it }, "Contraseña", esClave = true)
                TextoError(error)

                Button(
                    onClick = {
                        viewModel.iniciarSesion(correo, clave) { resultado ->
                            if (resultado == null) {
                                navController.irAInicio()
                            } else {
                                error = resultado
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Ingresar") }
            }
        }

        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("¿No tienes cuenta?", fontSize = 13.sp)
            TextButton(onClick = { navController.navigate(Destinos.REGISTRO) }) {
                Text("Regístrate", color = GoldYellow, fontWeight = FontWeight.Bold)
            }
        }
    }
}