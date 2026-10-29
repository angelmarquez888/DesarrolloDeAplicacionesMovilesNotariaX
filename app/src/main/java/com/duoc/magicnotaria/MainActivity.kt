package com.duoc.magicnotaria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.duoc.magicnotaria.model.Destinos
import com.duoc.magicnotaria.ui.theme.screens.*
import com.duoc.magicnotaria.ui.theme.MagicNotariaTheme
import com.duoc.magicnotaria.ui.theme.screens.DocumentosScreen
import com.duoc.magicnotaria.ui.theme.screens.InicioScreen
import com.duoc.magicnotaria.ui.theme.screens.LoginScreen
import com.duoc.magicnotaria.ui.theme.screens.PantallaSimple
import com.duoc.magicnotaria.ui.theme.screens.PerfilScreen
import com.duoc.magicnotaria.ui.theme.screens.RegistroScreen
import com.duoc.magicnotaria.ui.theme.screens.TramitesScreen
import com.duoc.magicnotaria.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MagicNotariaTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNotaria(viewModel)
                }
            }
        }
    }
}

@Composable
fun AppNotaria(viewModel: MainViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Destinos.LOGIN) {
        composable(Destinos.LOGIN) { LoginScreen(navController, viewModel) }
        composable(Destinos.REGISTRO) { RegistroScreen(navController, viewModel) }
        composable(Destinos.INICIO) { InicioScreen(navController, viewModel) }
        composable(Destinos.TRAMITES) { TramitesScreen(navController, viewModel) }
        composable(Destinos.DOCUMENTOS) { DocumentosScreen(navController, viewModel) }
        composable(Destinos.PERFIL) { PerfilScreen(navController, viewModel) }
        composable(Destinos.FIRMAS) { PantallaSimple(navController, "Firmas Pendientes") }
        composable(Destinos.CITA) { PantallaSimple(navController, "Agendar Cita") }
    }
}