package com.duoc.magicnotaria.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.duoc.magicnotaria.ui.components.CampoTexto
import com.duoc.magicnotaria.ui.components.TextoError
import com.duoc.magicnotaria.ui.components.irAInicio
import com.duoc.magicnotaria.viewmodel.MainViewModel

@Composable
fun RegistroScreen(navController: NavController, viewModel: MainViewModel) {
    var nombre by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().systemBarsPadding().imePadding()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
            Text(
                "Crear Cuenta", fontSize = 18.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f), textAlign = TextAlign.Center
            )
            Spacer(Modifier.width(48.dp))
        }

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CampoTexto(nombre, { nombre = it }, "Nombre Completo")
            CampoTexto(rut, { rut = it }, "RUT (ej: 12.345.678-9)")
            CampoTexto(correo, { correo = it }, "Correo Electrónico", teclado = KeyboardType.Email)
            CampoTexto(clave, { clave = it }, "Contraseña", esClave = true)
            CampoTexto(confirmar, { confirmar = it }, "Confirmar Contraseña", esClave = true)
            TextoError(error)

            Button(
                onClick = {
                    viewModel.registrar(nombre, rut, correo, clave, confirmar) { resultado ->
                        if (resultado == null) {
                            navController.irAInicio()
                        } else {
                            error = resultado
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) { Text("Registrarse") }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("¿Ya tienes cuenta?", fontSize = 13.sp)
                TextButton(onClick = { navController.popBackStack() }) {
                    Text("Inicia Sesión", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}