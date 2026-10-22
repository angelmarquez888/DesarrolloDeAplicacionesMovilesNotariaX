package com.duoc.magicnotaria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.duoc.magicnotaria.model.Destinos
import com.duoc.magicnotaria.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()

               
                NavHost(navController = navController, startDestination = Destinos.LOGIN) {

                    composable(Destinos.LOGIN) {
                        LoginBasico(navController, viewModel)
                    }
                    composable(Destinos.REGISTRO) {
                        PantallaSimple(navController, "Crear Cuenta")
                    }
                    composable(Destinos.INICIO) {
                        MenuInicio(navController)
                    }
                    composable(Destinos.TRAMITES) {
                        PantallaSimple(navController, "Catálogo de Trámites")
                    }
                    composable(Destinos.DOCUMENTOS) {
                        PantallaSimple(navController, "Mis Documentos")
                    }
                    composable(Destinos.PERFIL) {
                        PantallaSimple(navController, "Mi Perfil")
                    }
                }
            }
        }
    }
}



@Composable
fun LoginBasico(navController: NavController, viewModel: MainViewModel) {
    var email by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Notaría Digital", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo") })
        OutlinedTextField(value = clave, onValueChange = { clave = it }, label = { Text("Contraseña") })

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if(viewModel.intentarLogin(email, clave)) {
                navController.navigate(Destinos.INICIO)
            }
        }) { Text("Ingresar") }

        TextButton(onClick = { navController.navigate(Destinos.REGISTRO) }) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}

@Composable
fun MenuInicio(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Hola, Angel (Inicio)", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { navController.navigate(Destinos.TRAMITES) }, modifier = Modifier.fillMaxWidth()) { Text("Ir a Trámites") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { navController.navigate(Destinos.DOCUMENTOS) }, modifier = Modifier.fillMaxWidth()) { Text("Ir a Mis Documentos") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { navController.navigate(Destinos.PERFIL) }, modifier = Modifier.fillMaxWidth()) { Text("Ir a Mi Perfil") }

        Spacer(modifier = Modifier.height(32.dp))
        TextButton(onClick = { navController.navigate(Destinos.LOGIN) { popUpTo(0) } }) {
            Text("Cerrar Sesión", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun PantallaSimple(navController: NavController, titulo: String) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(titulo, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Volver Atrás")
        }
    }
}