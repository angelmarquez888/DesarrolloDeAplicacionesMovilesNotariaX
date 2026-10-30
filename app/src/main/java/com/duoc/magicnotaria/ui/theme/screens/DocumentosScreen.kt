package com.duoc.magicnotaria.ui.theme.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.duoc.magicnotaria.model.Destinos
import com.duoc.magicnotaria.model.Documento
import com.duoc.magicnotaria.ui.theme.components.*
import com.duoc.magicnotaria.ui.theme.*
import com.duoc.magicnotaria.viewmodel.MainViewModel

@Composable
fun DocumentosScreen(navController: NavController, viewModel: MainViewModel) {
    val context = LocalContext.current
    val documentos = viewModel.documentosFiltrados

    var documentoPorFirmar by remember { mutableStateOf<Documento?>(null) }

    PantallaConBarra(navController, Destinos.DOCUMENTOS) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            EncabezadoClaro("Mis Documentos") {
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = viewModel.busqueda,
                    onValueChange = viewModel::cambiarBusqueda,
                    placeholder = { Text("Buscar documento...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = GoldYellow,
                        unfocusedContainerColor = GrisCampo,
                        focusedContainerColor = GrisCampo
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (documentos.isEmpty()) {
                Text("No se encontraron documentos", color = GrisTexto, modifier = Modifier.padding(24.dp))
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(documentos, key = { it.id }) { doc ->
                    TarjetaDocumento(
                        doc = doc,
                        alVer = { mostrarMensaje(context, "Abriendo ${doc.titulo}") },
                        alDescargar = { mostrarMensaje(context, "Descargando ${doc.titulo}") },
                        alFirmar = { documentoPorFirmar = doc } // abre el modal
                    )
                }
            }
        }
    }

    documentoPorFirmar?.let { doc ->
        DialogoConfirmacion(
            titulo = "Firmar documento",
            texto = "¿Deseas firmar «${doc.titulo}»? Esta acción no se puede deshacer.",
            alConfirmar = {
                viewModel.firmarDocumento(doc.id)
                mostrarMensaje(context, "Documento firmado correctamente")
                documentoPorFirmar = null
            },
            alCancelar = { documentoPorFirmar = null }
        )
    }
}



@Composable
private fun TarjetaDocumento(
    doc: Documento,
    alVer: () -> Unit,
    alDescargar: () -> Unit,
    alFirmar: () -> Unit
) {
    TarjetaBlanca {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(if (doc.firmado) RojoSuave else AmarilloSuave, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (doc.firmado) {
                        Text("PDF", color = RojoPdf, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.Create, contentDescription = null, tint = GoldYellow)
                    }
                }
                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(doc.titulo, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkBlue)
                    Text(doc.detalle, fontSize = 12.sp, color = GrisTexto)
                }

                EtiquetaEstado(doc.firmado)
            }

            if (doc.firmado) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BotonGris("Ver", Icons.Default.Search, Modifier.weight(1f), alVer)
                    BotonGris("Descargar", Icons.Default.KeyboardArrowDown, Modifier.weight(1f), alDescargar)
                }
            } else {
                Button(
                    onClick = alFirmar,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldYellow, contentColor = Color.White)
                ) {
                    Icon(Icons.Default.Create, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Firmar Documento", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/** Etiqueta verde "FIRMADO" o amarilla "PENDIENTE". */
@Composable
private fun EtiquetaEstado(firmado: Boolean) {
    Text(
        text = if (firmado) "FIRMADO" else "PENDIENTE",
        color = if (firmado) VerdeOk else AmarilloTexto,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(if (firmado) VerdeSuave else AmarilloSuave, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}

@Composable
private fun BotonGris(texto: String, icono: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = GrisCampo, contentColor = DarkBlue)
    ) {
        Icon(icono, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(texto, fontSize = 13.sp)
    }
}