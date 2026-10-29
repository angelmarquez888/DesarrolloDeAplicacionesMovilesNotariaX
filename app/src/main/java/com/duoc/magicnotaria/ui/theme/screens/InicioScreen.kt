package com.duoc.magicnotaria.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.duoc.magicnotaria.model.Destinos
import com.duoc.magicnotaria.ui.components.PantallaConBarra
import com.duoc.magicnotaria.ui.components.TarjetaBlanca
import com.duoc.magicnotaria.ui.components.TituloMagicNotaria
import com.duoc.magicnotaria.ui.theme.DarkBlue
import com.duoc.magicnotaria.ui.theme.VerdeOk
import com.duoc.magicnotaria.viewmodel.MainViewModel

private val accesosRapidos = listOf(
    Triple("Iniciar Trámite", Icons.Default.Edit, Destinos.TRAMITES),
    Triple("Mis Documentos", Icons.AutoMirrored.Filled.List, Destinos.DOCUMENTOS),
    Triple("Firmas Pendientes", Icons.Default.Create, Destinos.FIRMAS),
    Triple("Agendar Cita", Icons.Default.DateRange, Destinos.CITA)
)

@Composable
fun InicioScreen(navController: NavController, viewModel: MainViewModel) {
    val nombre = viewModel.usuarioActual?.nombre?.substringBefore(" ") ?: "Usuario"

    PantallaConBarra(navController, Destinos.INICIO) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkBlue, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .statusBarsPadding()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TituloMagicNotaria(colorMagic = Color.White, tamano = 20.sp)
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
                }
                Spacer(Modifier.height(20.dp))
                Text("Bienvenido de vuelta,", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                Text("Hola, $nombre", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            }

            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaBlanca {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("ESTADO DE CUENTA", fontSize = 11.sp, color = Color.Gray)
                            Text("Identidad Verificada", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("Firma electrónica activa", fontSize = 12.sp, color = VerdeOk)
                        }
                        Icon(
                            Icons.Default.CheckCircle, contentDescription = null,
                            tint = VerdeOk, modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Text("Accesos Rápidos", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                accesosRapidos.chunked(2).forEach { fila ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        fila.forEach { (titulo, icono, ruta) ->
                            AccesoRapido(titulo, icono, Modifier.weight(1f)) { navController.navigate(ruta) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AccesoRapido(titulo: String, icono: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icono, contentDescription = null, tint = DarkBlue)
            Spacer(Modifier.height(6.dp))
            Text(titulo, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkBlue)
        }
    }
}