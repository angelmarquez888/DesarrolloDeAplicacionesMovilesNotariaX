package com.duoc.magicnotaria.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.duoc.magicnotaria.model.Destinos
import com.duoc.magicnotaria.model.Documento
import com.duoc.magicnotaria.ui.theme.*
import com.duoc.magicnotaria.ui.theme.components.*
import com.duoc.magicnotaria.viewmodel.MainViewModel

@Composable
fun FirmasScreen(navController: NavController, viewModel: MainViewModel) {
    val context = LocalContext.current
    val documentosPendientes = viewModel.documentosPendientes

    var documentoPorFirmar by remember { mutableStateOf<Documento?>(null) }
    var documentoPorCancelar by remember { mutableStateOf<Documento?>(null) }

    PantallaConBarra(navController, Destinos.FIRMAS) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            EncabezadoClaro("Firmas Pendientes", "Documentos que esperan tu firma")
            EstadoDeRed(viewModel.cargando, viewModel.mensajeError)

            if (documentosPendientes.isEmpty() && !viewModel.cargando) {
                Text("No tienes firmas pendientes", color = GrisTexto, modifier = Modifier.padding(24.dp))
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(documentosPendientes, key = { it.id }) { documento ->
                    TarjetaFirmaPendiente(
                        documento = documento,
                        alFirmar = { documentoPorFirmar = documento },
                        alCancelar = { documentoPorCancelar = documento }
                    )
                }
            }
        }
    }

    documentoPorFirmar?.let { documento ->
        DialogoConfirmacion(
            titulo = "Firmar documento",
            texto = "¿Deseas firmar «${documento.titulo}»? Esta acción no se puede deshacer.",
            alConfirmar = {
                viewModel.firmarDocumento(documento) { error ->
                    mostrarMensaje(context, error ?: "Documento firmado correctamente")
                }
                documentoPorFirmar = null
            },
            alCancelar = { documentoPorFirmar = null }
        )
    }

    documentoPorCancelar?.let { documento ->
        DialogoConfirmacion(
            titulo = "Cancelar trámite",
            texto = "¿Deseas cancelar «${documento.titulo}»? El documento se eliminará de tu cuenta.",
            alConfirmar = {
                viewModel.eliminarDocumento(documento) { error ->
                    mostrarMensaje(context, error ?: "Trámite cancelado")
                }
                documentoPorCancelar = null
            },
            alCancelar = { documentoPorCancelar = null }
        )
    }
}

@Composable
private fun TarjetaFirmaPendiente(
    documento: Documento,
    alFirmar: () -> Unit,
    alCancelar: () -> Unit
) {
    TarjetaBlanca {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(documento.titulo, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkBlue)
            Text(documento.detalle, fontSize = 12.sp, color = GrisTexto)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = alFirmar,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldYellow, contentColor = Color.White)
                ) {
                    Icon(Icons.Default.Create, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Firmar", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = alCancelar,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RojoSuave, contentColor = RojoPdf)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Cancelar", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}