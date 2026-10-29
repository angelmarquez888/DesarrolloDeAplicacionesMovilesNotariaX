package com.duoc.magicnotaria.ui.theme.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.duoc.magicnotaria.model.Destinos
import com.duoc.magicnotaria.ui.components.*
import com.duoc.magicnotaria.ui.theme.*
import com.duoc.magicnotaria.viewmodel.MainViewModel

@Composable
fun PerfilScreen(navController: NavController, viewModel: MainViewModel) {
    val usuario = viewModel.usuarioActual
    val context = LocalContext.current
    val inicial = usuario?.nombre?.firstOrNull()?.uppercase() ?: "?"

    var confirmarSalida by remember { mutableStateOf(false) } // controla el modal de cerrar sesión


    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { foto ->
        foto?.let { viewModel.cambiarFoto(it) }
    }

    PantallaConBarra(navController, Destinos.PERFIL) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkBlue, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .statusBarsPadding()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Mi Perfil", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TarjetaBlanca {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        FotoPerfil(viewModel.fotoPerfil, inicial) { camara.launch(null) }
                        Text("Toca la foto para cambiarla", fontSize = 11.sp, color = GrisTexto)
                        Spacer(Modifier.height(8.dp))
                        Text(usuario?.nombre ?: "-", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Usuario Verificado", fontSize = 13.sp, color = GrisTexto)
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                Icons.Default.CheckCircle, contentDescription = null,
                                tint = VerdeOk, modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        HorizontalDivider()
                        FilaDato("RUT", usuario?.rut ?: "-")
                        FilaDato("CORREO", usuario?.correo ?: "-")
                        FilaDato("DIRECCIÓN", usuario?.direccion ?: "-")
                    }
                }

                TarjetaBlanca {
                    FilaOpcion(Icons.Default.Lock, "Seguridad y Privacidad") { mostrarMensaje(context, "Próximamente") }
                    FilaOpcion(Icons.Default.Notifications, "Notificaciones") { mostrarMensaje(context, "Próximamente") }
                    FilaOpcion(Icons.Default.Info, "Centro de Ayuda") { mostrarMensaje(context, "Próximamente") }
                }

                Button(
                    onClick = { confirmarSalida = true },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RojoSuave, contentColor = RojoPdf)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
                }
            }
        }
    }


    if (confirmarSalida) {
        DialogoConfirmacion(
            titulo = "Cerrar sesión",
            texto = "¿Seguro que quieres salir de MagicNotaria?",
            alConfirmar = {
                viewModel.cerrarSesion()
                navController.navigate(Destinos.LOGIN) { popUpTo(0) } // popUpTo(0) borra todo el historial
            },
            alCancelar = { confirmarSalida = false }
        )
    }
}

/** Círculo con la foto del usuario; si aún no tiene foto, muestra la inicial de su nombre. */
@Composable
private fun FotoPerfil(foto: Bitmap?, inicial: String, alTocar: () -> Unit) {
    Box(
        modifier = Modifier.size(80.dp).clip(CircleShape).background(GrisCampo).clickable(onClick = alTocar),
        contentAlignment = Alignment.Center
    ) {
        if (foto != null) {
            Image(
                bitmap = foto.asImageBitmap(),
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop, // recorta la foto para llenar el círculo
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(inicial, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
        }
    }
}

@Composable
private fun FilaDato(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(etiqueta, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GrisTexto)
        Text(valor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarkBlue)
    }
}

@Composable
private fun FilaOpcion(icono: ImageVector, texto: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, contentDescription = null, tint = DarkBlue)
        Spacer(Modifier.width(12.dp))
        Text(texto, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium, color = DarkBlue)
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
    }
}