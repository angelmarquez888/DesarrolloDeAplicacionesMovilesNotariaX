package com.duoc.magicnotaria

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.duoc.magicnotaria.model.CategoriaTramite
import com.duoc.magicnotaria.model.Destinos
import com.duoc.magicnotaria.model.Documento
import com.duoc.magicnotaria.ui.theme.*
import com.duoc.magicnotaria.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MagicNotariaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
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

fun mensaje(context: Context, texto: String) {
    Toast.makeText(context, texto, Toast.LENGTH_SHORT).show()
}

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
        if (subtitulo != null) {
            Text(subtitulo, fontSize = 13.sp, color = GrisTexto)
        }
        extra()
    }
}

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
fun BarraInferior(navController: NavController, rutaActual: String) {
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
                        popUpTo(Destinos.INICIO)
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

@Composable
fun LoginScreen(navController: NavController, viewModel: MainViewModel) {
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding().imePadding().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(72.dp).background(DarkBlue, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("⚖", color = GoldYellow, fontSize = 36.sp)
        }
        Spacer(Modifier.height(16.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = DarkBlue)) { append("Notaría") }
                withStyle(SpanStyle(color = GoldYellow)) { append("Digital") }
            },
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text("Tu notaría en la palma de tu mano", fontSize = 12.sp, color = Color.Gray)
        Spacer(Modifier.height(32.dp))

        TarjetaBlanca {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Iniciar Sesión", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                CampoTexto(correo, { correo = it }, "Correo electrónico", teclado = KeyboardType.Email)
                CampoTexto(clave, { clave = it }, "Contraseña", esClave = true)

                if (error.isNotEmpty()) {
                    Text(error, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                }

                Button(
                    onClick = {
                        val resultado = viewModel.intentarLogin(correo, clave)
                        if (resultado == null) {
                            navController.navigate(Destinos.INICIO) {
                                popUpTo(Destinos.LOGIN) { inclusive = true }
                            }
                        } else {
                            error = resultado
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Ingresar") }
            }
        }

        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("¿No tienes cuenta?", fontSize = 13.sp)
            TextButton(onClick = { navController.navigate(Destinos.REGISTRO) }) {
                Text("Regístrate", color = GoldYellow, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun RegistroScreen(navController: NavController, viewModel: MainViewModel) {
    var nombre by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

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
            CampoTexto(rut, { rut = it }, "RUT / Documento de Identidad")
            CampoTexto(correo, { correo = it }, "Correo Electrónico", teclado = KeyboardType.Email)
            CampoTexto(clave, { clave = it }, "Contraseña", esClave = true)
            CampoTexto(confirmar, { confirmar = it }, "Confirmar Contraseña", esClave = true)

            if (error.isNotEmpty()) {
                Text(error, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            Button(
                onClick = {
                    val resultado = viewModel.registrar(nombre, rut, correo, clave, confirmar)
                    if (resultado == null) {
                        navController.navigate(Destinos.INICIO) {
                            popUpTo(Destinos.LOGIN) { inclusive = true }
                        }
                    } else {
                        error = resultado
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
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = Color.White)) { append("Notaría") }
                            withStyle(SpanStyle(color = GoldYellow)) { append("Digital") }
                        },
                        fontWeight = FontWeight.Bold, fontSize = 18.sp
                    )
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
                }
                Spacer(Modifier.height(20.dp))
                Text("Bienvenido de vuelta,", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                Text("Hola, $nombre", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            }

            Column(modifier = Modifier.padding(20.dp)) {
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

                Spacer(Modifier.height(24.dp))
                Text("Accesos Rápidos", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AccesoRapido("Iniciar Trámite", Icons.Default.Edit, Modifier.weight(1f)) {
                        navController.navigate(Destinos.TRAMITES)
                    }
                    AccesoRapido("Mis Documentos", Icons.AutoMirrored.Filled.List, Modifier.weight(1f)) {
                        navController.navigate(Destinos.DOCUMENTOS)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AccesoRapido("Firmas Pendientes", Icons.Default.Create, Modifier.weight(1f)) {
                        navController.navigate(Destinos.FIRMAS)
                    }
                    AccesoRapido("Agendar Cita", Icons.Default.DateRange, Modifier.weight(1f)) {
                        navController.navigate(Destinos.CITA)
                    }
                }
            }
        }
    }
}

@Composable
fun AccesoRapido(titulo: String, icono: ImageVector, modifier: Modifier, onClick: () -> Unit) {
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
                    TarjetaCategoria(categoria) { nombre ->
                        mensaje(context, "Iniciando: $nombre")
                    }
                }
            }
        }
    }
}

@Composable
fun IconoCategoria(titulo: String) {
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

@Composable
fun TarjetaCategoria(categoria: CategoriaTramite, alElegir: (String) -> Unit) {
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

            categoria.tramites.forEach { nombre ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(FondoClaro)
                        .clickable { alElegir(nombre) }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(nombre, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = DarkBlue)
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun DocumentosScreen(navController: NavController, viewModel: MainViewModel) {
    val context = LocalContext.current
    val documentos = viewModel.documentosFiltrados

    PantallaConBarra(navController, Destinos.DOCUMENTOS) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            EncabezadoClaro("Mis Documentos") {
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = viewModel.busqueda,
                    onValueChange = { viewModel.cambiarBusqueda(it) },
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
                Text(
                    "No se encontraron documentos",
                    color = GrisTexto,
                    modifier = Modifier.padding(24.dp)
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(documentos, key = { it.id }) { doc ->
                    TarjetaDocumento(
                        doc = doc,
                        alVer = { mensaje(context, "Abriendo ${doc.titulo}") },
                        alDescargar = { mensaje(context, "Descargando ${doc.titulo}") },
                        alFirmar = {
                            viewModel.firmarDocumento(doc.id)
                            mensaje(context, "Documento firmado correctamente")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TarjetaDocumento(
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
                // Icono: "PDF" si está firmado, lápiz si está pendiente
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

                Text(
                    text = if (doc.firmado) "FIRMADO" else "PENDIENTE",
                    color = if (doc.firmado) VerdeOk else AmarilloTexto,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(
                            if (doc.firmado) VerdeSuave else AmarilloSuave,
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
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

@Composable
fun BotonGris(texto: String, icono: ImageVector, modifier: Modifier, onClick: () -> Unit) {
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

@Composable
fun PerfilScreen(navController: NavController, viewModel: MainViewModel) {
    val usuario = viewModel.usuarioActual
    val context = LocalContext.current
    val inicial = usuario?.nombre?.firstOrNull()?.uppercase() ?: "?"

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
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                TarjetaBlanca {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.size(72.dp).background(GrisCampo, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(inicial, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
                        }
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
                    FilaOpcion(Icons.Default.Lock, "Seguridad y Privacidad") {
                        mensaje(context, "Próximamente")
                    }
                    FilaOpcion(Icons.Default.Notifications, "Notificaciones") {
                        mensaje(context, "Próximamente")
                    }
                    FilaOpcion(Icons.Default.Info, "Centro de Ayuda") {
                        mensaje(context, "Próximamente")
                    }
                }

                Button(
                    onClick = {
                        viewModel.cerrarSesion()
                        navController.navigate(Destinos.LOGIN) { popUpTo(0) }
                    },
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
}

@Composable
fun FilaDato(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(etiqueta, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GrisTexto)
        Text(valor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarkBlue)
    }
}

@Composable
fun FilaOpcion(icono: ImageVector, texto: String, onClick: () -> Unit) {
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

@Composable
fun PantallaSimple(navController: NavController, titulo: String) {
    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(titulo, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))
        Button(onClick = { navController.popBackStack() }) { Text("Volver Atrás") }
    }
}