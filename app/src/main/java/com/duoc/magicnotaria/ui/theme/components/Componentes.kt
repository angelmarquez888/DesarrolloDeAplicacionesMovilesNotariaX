package com.duoc.magicnotaria.ui.theme.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.duoc.magicnotaria.R
import com.duoc.magicnotaria.model.Destinos
import com.duoc.magicnotaria.ui.theme.DarkBlue
import com.duoc.magicnotaria.ui.theme.GoldYellow
import com.duoc.magicnotaria.ui.theme.GrisTexto


/** Muestra un mensaje corto flotante (Toast) en la parte baja de la pantalla. */
fun mostrarMensaje(context: Context, texto: String) {
    Toast.makeText(context, texto, Toast.LENGTH_SHORT).show()
}

/** Navega al inicio y borra login/registro del historial para que "atrás" no vuelva a ellos. */
fun NavController.irAInicio() {
    navigate(Destinos.INICIO) { popUpTo(Destinos.LOGIN) { inclusive = true } }
}

/** Campo de texto con borde. Si esClave es true, oculta lo escrito con puntos. */
@Composable
fun CampoTexto(
    valor: String,
    alCambiar: (String) -> Unit,
    etiqueta: String,
    esClave: Boolean = false,
    teclado: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        label = { Text(etiqueta) },
        singleLine = true,
        visualTransformation = if (esClave) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        modifier = Modifier.fillMaxWidth()
    )
}

/** Muestra el mensaje de error en rojo; si no hay error (null) no dibuja nada. */
@Composable
fun TextoError(texto: String?) {
    texto?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp) }
}

/** Tarjeta blanca con esquinas redondeadas y sombra suave. */
@Composable
fun TarjetaBlanca(modifier: Modifier = Modifier, contenido: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        content = contenido
    )
}

/** Imagen del logo (ic_logo.xml) sobre un cuadro azul oscuro. */
@Composable
fun LogoMagicNotaria() {
    Box(
        modifier = Modifier.size(88.dp).background(DarkBlue, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_logo),
            contentDescription = "Logo de MagicNotaria",
            modifier = Modifier.size(56.dp)
        )
    }
}

/** Nombre de la app en dos colores: "Magic" (colorMagic) + "Notaria" (dorado). */
@Composable
fun TituloMagicNotaria(colorMagic: Color, tamano: TextUnit) {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = colorMagic)) { append("Magic") }
            withStyle(SpanStyle(color = GoldYellow)) { append("Notaria") }
        },
        fontSize = tamano,
        fontWeight = FontWeight.Bold
    )
}

/** Encabezado blanco con título, subtítulo opcional y contenido extra (ej: buscador). */
@Composable
fun EncabezadoClaro(
    titulo: String,
    subtitulo: String? = null,
    extra: @Composable () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth().background(Color.White).statusBarsPadding().padding(20.dp)
    ) {
        Text(titulo, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
        subtitulo?.let { Text(it, fontSize = 13.sp, color = GrisTexto) }
        extra()
    }
}

/** MODAL: ventana emergente que pide confirmar o cancelar una acción importante. */
@Composable
fun DialogoConfirmacion(
    titulo: String,
    texto: String,
    alConfirmar: () -> Unit,
    alCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = alCancelar, // se llama al tocar fuera del cuadro o al volver atrás
        title = { Text(titulo, fontWeight = FontWeight.Bold) },
        text = { Text(texto) },
        confirmButton = { TextButton(onClick = alConfirmar) { Text("Confirmar") } },
        dismissButton = { TextButton(onClick = alCancelar) { Text("Cancelar") } }
    )
}

/** Estructura de las pantallas principales: contenido + barra de navegación inferior. */
@Composable
fun PantallaConBarra(
    navController: NavController,
    rutaActual: String,
    contenido: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = { BarraInferior(navController, rutaActual) },
        content = contenido
    )
}

@Composable
private fun BarraInferior(navController: NavController, rutaActual: String) {
    val items = listOf(
        Triple(Destinos.INICIO, "Inicio", Icons.Default.Home),
        Triple(Destinos.TRAMITES, "Trámites", Icons.Default.Edit),
        Triple(Destinos.DOCUMENTOS, "Documentos", Icons.AutoMirrored.Filled.List),
        Triple(Destinos.PERFIL, "Perfil", Icons.Default.Person)
    )

    NavigationBar(containerColor = Color.White) {
        items.forEach { (ruta, titulo, icono) ->
            NavigationBarItem(
                selected = ruta == rutaActual,
                onClick = {
                    navController.navigate(ruta) {
                        popUpTo(Destinos.INICIO) // evita apilar pantallas repetidas
                        launchSingleTop = true
                    }
                },
                icon = { Icon(icono, contentDescription = titulo) },
                label = { Text(titulo, fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GoldYellow,
                    selectedTextColor = GoldYellow,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}