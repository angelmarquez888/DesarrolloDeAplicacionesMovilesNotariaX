package com.duoc.magicnotaria.ui.theme.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.duoc.magicnotaria.model.Cita
import com.duoc.magicnotaria.model.Destinos
import com.duoc.magicnotaria.ui.theme.*
import com.duoc.magicnotaria.ui.theme.components.*
import com.duoc.magicnotaria.viewmodel.MainViewModel
import java.util.Calendar

@Composable
fun CitaScreen(navController: NavController, viewModel: MainViewModel) {
    val context = LocalContext.current

    var motivo by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var citaPorCancelar by remember { mutableStateOf<Cita?>(null) }

    PantallaConBarra(navController, Destinos.CITA) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            EncabezadoClaro("Agendar Cita", "Reserva tu atención en la notaría")
            EstadoDeRed(viewModel.cargando, viewModel.mensajeError)

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    FormularioCita(
                        motivo = motivo,
                        fecha = fecha,
                        hora = hora,
                        alCambiarMotivo = { motivo = it },
                        alElegirFecha = { elegirFecha(context) { fecha = it } },
                        alElegirHora = { elegirHora(context) { hora = it } },
                        alAgendar = {
                            viewModel.agendarCita(motivo, fecha, hora) { error ->
                                if (error == null) {
                                    motivo = ""
                                    fecha = ""
                                    hora = ""
                                }
                                mostrarMensaje(context, error ?: "Cita agendada correctamente")
                            }
                        }
                    )
                }

                item {
                    Text("Mis citas", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
                }

                if (viewModel.citas.isEmpty() && !viewModel.cargando) {
                    item { Text("No tienes citas agendadas", color = GrisTexto) }
                }

                items(viewModel.citas, key = { it.id }) { cita ->
                    TarjetaCita(cita = cita, alCancelar = { citaPorCancelar = cita })
                }
            }
        }
    }

    citaPorCancelar?.let { cita ->
        DialogoConfirmacion(
            titulo = "Cancelar cita",
            texto = "¿Deseas cancelar la cita del ${cita.fecha} a las ${cita.hora}?",
            alConfirmar = {
                viewModel.cancelarCita(cita) { error ->
                    mostrarMensaje(context, error ?: "Cita cancelada")
                }
                citaPorCancelar = null
            },
            alCancelar = { citaPorCancelar = null }
        )
    }
}

@Composable
private fun FormularioCita(
    motivo: String,
    fecha: String,
    hora: String,
    alCambiarMotivo: (String) -> Unit,
    alElegirFecha: () -> Unit,
    alElegirHora: () -> Unit,
    alAgendar: () -> Unit
) {
    TarjetaBlanca {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CampoTexto(motivo, alCambiarMotivo, "Motivo de la cita")

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = alElegirFecha, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(fecha.ifEmpty { "Fecha" })
                }
                OutlinedButton(onClick = alElegirHora, modifier = Modifier.weight(1f)) {
                    Text(hora.ifEmpty { "Hora" })
                }
            }

            Button(onClick = alAgendar, modifier = Modifier.fillMaxWidth()) {
                Text("Agendar Cita")
            }
        }
    }
}

@Composable
private fun TarjetaCita(cita: Cita, alCancelar: () -> Unit) {
    TarjetaBlanca {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(cita.motivo, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkBlue)
                Text("${cita.fecha} • ${cita.hora}", fontSize = 12.sp, color = GrisTexto)
            }
            IconButton(onClick = alCancelar) {
                Icon(Icons.Default.Delete, contentDescription = "Cancelar cita", tint = RojoPdf)
            }
        }
    }
}

private fun elegirFecha(context: Context, alElegir: (String) -> Unit) {
    val hoy = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, anio, mes, dia -> alElegir("%02d/%02d/%d".format(dia, mes + 1, anio)) },
        hoy.get(Calendar.YEAR),
        hoy.get(Calendar.MONTH),
        hoy.get(Calendar.DAY_OF_MONTH)
    ).apply {
        datePicker.minDate = hoy.timeInMillis - 1000
    }.show()
}

private fun elegirHora(context: Context, alElegir: (String) -> Unit) {
    TimePickerDialog(
        context,
        { _, hora, minuto -> alElegir("%02d:%02d".format(hora, minuto)) },
        9, 0, true
    ).show()
}