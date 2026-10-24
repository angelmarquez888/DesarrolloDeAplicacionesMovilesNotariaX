package com.duoc.magicnotaria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.duoc.magicnotaria.model.Destinos
import com.duoc.magicnotaria.ui.theme.DarkBlue
import com.duoc.magicnotaria.ui.theme.GoldYellow
import com.duoc.magicnotaria.ui.theme.MagicNotariaTheme
import com.duoc.magicnotaria.ui.theme.VerdeOk
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

// ---------- NAVEGACIÓN ----------
@Composable
fun AppNotaria(viewModel: MainViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Destinos.LOGIN) {
        composable(Destinos.LOGIN) { LoginScreen(navController, viewModel) }
        composable(Destinos.REGISTRO) { RegistroScreen(navController, viewModel) }
        composable(Destinos.INICIO) { InicioScreen(navController, viewModel) }
        composable(Destinos.PERFIL) { PerfilScreen(navController, viewModel) }
        composable(Destinos.TRAMITES) { PantallaSimple(navController, "Catálogo de Trámites") }
        composable(Destinos.DOCUMENTOS) { PantallaSimple(navController, "Mis Documentos") }
        composable(Destinos.FIRMAS) { PantallaSimple(navController, "Firmas Pendientes") }
        composable(Destinos.CITA) { PantallaSimple(navController, "Agendar Cita") }
    }
}

// ---------- COMPONENTES REUTILIZABLES ----------
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
        visualTransformation = if (esClave) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        modifier = Modifier.fillMaxWidth()
    )
}

// ---------- PANTALLA 1: LOGIN ----------
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
        // Logo
        Box(
            modifier = Modifier.size(72.dp).background(DarkBlue, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("⚖", color = GoldYellow, fontSize = 36.sp)
        }
        Spacer(Modifier.height(16.dp))

        // Título con dos colores
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

        // Tarjeta blanca con el formulario
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
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

// ---------- PANTALLA 2: CREAR CUENTA ----------
@Composable
fun RegistroScreen(navController: NavController, viewModel: MainViewModel) {
    var nombre by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().systemBarsPadding().imePadding()) {
        // Barra superior con flecha y título
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
            Text(
                "Crear Cuenta", fontSize = 18.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(Modifier.width(48.dp)) // equilibra el espacio de la flecha
        }

        // Formulario (con scroll por si el teclado tapa campos)
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

// ---------- PANTALLA 3: INICIO ----------
@Composable
fun InicioScreen(navController: NavController, viewModel: MainViewModel) {
    // Toma solo el primer nombre: "Angel Marquez" -> "Angel"
    val nombre = viewModel.usuarioActual?.nombre?.substringBefore(" ") ?: "Usuario"

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = { BarraInferior(navController) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // Encabezado azul oscuro con esquinas redondeadas abajo
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
                // Tarjeta de estado de cuenta
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
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
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = VerdeOk, modifier = Modifier.size(40.dp))
                    }
                }

                Spacer(Modifier.height(24.dp))
                Text("Accesos Rápidos", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))

                // Cuadrícula de 2x2
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
fun BarraInferior(navController: NavController) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Inicio") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GoldYellow,
                selectedTextColor = GoldYellow,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Destinos.TRAMITES) },
            icon = { Icon(Icons.Default.Edit, contentDescription = null) },
            label = { Text("Trámites") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Destinos.DOCUMENTOS) },
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
            label = { Text("Documentos") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Destinos.PERFIL) },
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Perfil") }
        )
    }
}

// ---------- OTRAS PANTALLAS ----------
@Composable
fun PerfilScreen(navController: NavController, viewModel: MainViewModel) {
    val usuario = viewModel.usuarioActual

    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Mi Perfil", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))
        Text("Nombre: ${usuario?.nombre ?: "-"}")
        Text("RUT: ${usuario?.rut ?: "-"}")
        Text("Correo: ${usuario?.correo ?: "-"}")
        Spacer(Modifier.height(24.dp))

        Button(onClick = { navController.popBackStack() }) { Text("Volver Atrás") }
        TextButton(onClick = {
            viewModel.cerrarSesion()
            navController.navigate(Destinos.LOGIN) { popUpTo(0) }
        }) {
            Text("Cerrar Sesión", color = MaterialTheme.colorScheme.error)
        }
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