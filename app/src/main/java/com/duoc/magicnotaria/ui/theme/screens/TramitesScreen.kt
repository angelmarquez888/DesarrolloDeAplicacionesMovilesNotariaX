package com.duoc.magicnotaria.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.duoc.magicnotaria.model.CategoriaTramite
import com.duoc.magicnotaria.model.Destinos
import com.duoc.magicnotaria.ui.theme.components.EncabezadoClaro
import com.duoc.magicnotaria.ui.theme.components.PantallaConBarra
import com.duoc.magicnotaria.ui.theme.components.TarjetaBlanca
import com.duoc.magicnotaria.ui.theme.components.mostrarMensaje
import com.duoc.magicnotaria.ui.theme.*
import com.duoc.magicnotaria.viewmodel.MainViewModel

@Composable
fun TramitesScreen(navController: NavController, viewModel: MainViewModel) {
    val context = LocalContext.current

    PantallaConBarra(navController, Destinos.TRAMITES) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            EncabezadoClaro("Catálogo de Trámites", "Selecciona el servicio que necesitas")

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(viewModel.categorias) { categoria ->
                    TarjetaCategoria(categoria) { tramite ->
                        mostrarMensaje(context, "Iniciando: $tramite")
                    }
                }
            }
        }
    }
}






@Composable
private fun TarjetaCategoria(categoria: CategoriaTramite, alElegir: (String) -> Unit) {
    TarjetaBlanca {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconoCategoria(categoria.titulo)
                Spacer(Modifier.width(12.dp))
                Text(categoria.titulo, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkBlue)
            }

            categoria.tramites.forEach { tramite ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(FondoClaro)
                        .clickable { alElegir(tramite) }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(tramite, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = DarkBlue)
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
                }
            }
        }
    }
}

@Composable
private fun IconoCategoria(titulo: String) {
    val (icono, fondo, color) = when (titulo) {
        "Vehículos" -> Triple(Icons.Default.Build, NaranjaSuave, NaranjaIcono)
        "Certificaciones" -> Triple(Icons.Default.CheckCircle, VerdeSuave, VerdeOk)
        else -> Triple(Icons.Default.Edit, AzulSuave, AzulIcono)
    }
    Box(
        modifier = Modifier.size(40.dp).background(fondo, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icono, contentDescription = null, tint = color)
    }
}