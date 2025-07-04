package com.example.appguitarra.ui.principal

import MastilVisual
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.appguitarra.R
import com.example.appguitarra.navigation.Rutas
import com.example.appguitarra.ui.theme.AppGuitarraTheme

import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Density
import kotlinx.coroutines.delay

import androidx.compose.ui.platform.LocalContext
import com.example.appguitarra.data.AppDatabase
import com.example.appguitarra.data.AppSesion
import kotlinx.coroutines.launch


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PantallaPrincipal(navController: NavHostController) {
    var submenuActivo by remember { mutableStateOf("Modos griegos") }
    var notaSeleccionada by remember { mutableStateOf("") }

    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()
    var porcentajeProgreso by remember { mutableStateOf(0f) }
    var refrescarRosco by remember { mutableStateOf(false) }



    LaunchedEffect(Unit) {
        try {
            val usuario = AppSesion.usuarioActual
            if (usuario == null) {
                Log.e("PantallaPrincipal", "Usuario nulo en AppSesion")
                return@LaunchedEffect
            }
            if (usuario.id == 0) {
                Log.e("PantallaPrincipal", "ID de usuario es 0")
                return@LaunchedEffect
            }

            val db = AppDatabase.getDatabase(contexto)
            val progreso = db.progresoDao().obtenerPorUsuario(usuario.id)
            val totalActividades = 3

            Log.d("PantallaPrincipal", "Actividades completadas: ${progreso.size}")
            porcentajeProgreso = progreso.size / totalActividades.toFloat()

        } catch (e: Exception) {
            Log.e("PantallaPrincipal", "Error al obtener progreso", e)
        }
    }




    Column(modifier = Modifier.fillMaxSize()) {
        // Menú superior
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp, start = 4.dp, end = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = Color(0xFFE7F0F9),
                shape = RoundedCornerShape(24.dp),
                tonalElevation = 2.dp,
                shadowElevation = 4.dp,
                modifier = Modifier.padding(8.dp)
            ) {
                FlowRow(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalArrangement = Arrangement.Center
                ) {

                    val opcionesSubmenu = listOf(
                        stringResource(R.string.submenu_modos_jonicos),
                        stringResource(R.string.submenu_armadura),
                        stringResource(R.string.submenu_modos_griegos)
                    )

                    opcionesSubmenu.forEach { item ->
                        val isSelected = submenuActivo == item
                        Button(
                            onClick = { submenuActivo = item },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) Color(0xFFD0E4F7) else Color(
                                    0xFFE7F0F9
                                ),
                                contentColor = Color(0xFF153B59)
                            ),
                            shape = RoundedCornerShape(50),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = item,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Contenido principal
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp, 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(720.dp)

            ) {
                Image(
                    painter = painterResource(id = R.drawable.mastil_fondo),
                    contentDescription = stringResource(R.string.descripcion_fondo_mastil),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(180.dp)
                        .height(720.dp)
                )

                MastilVisual(
                    notaSeleccionada = notaSeleccionada,
                    onNotaClick = { notaSeleccionada = it }
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Zona derecha
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.Top
            ) {
                // Botones de nota SIEMPRE visibles
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("C", "D", "E", "F", "G", "A", "B").forEach { nota ->
                        Button(
                            onClick = { notaSeleccionada = nota },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (notaSeleccionada == nota) Color(0xFFB4D4F0) else Color(
                                    0xFFE0ECF5
                                )
                            ),
                            shape = ShapePua(),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier
                                .size(56.dp)
                                .graphicsLayer(rotationZ = -140f)
                        ) {
                            Text(modifier = Modifier.graphicsLayer(rotationZ = 140f), text = nota, fontSize = 12.sp, color = Color(0xFF153B59))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val mensajeNota = if (notaSeleccionada.isNotEmpty()) {
                    stringResource(R.string.nota_en_diapason, notaSeleccionada)
                } else {
                    stringResource(R.string.pulsa_una_nota)
                }

                //Explicación de nota
                Text(
                    text = mensajeNota,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF153B59)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = when (notaSeleccionada) {
                        "E" -> stringResource(R.string.nota_info_e)
                        "A" -> stringResource(R.string.nota_info_a)
                        "D" -> stringResource(R.string.nota_info_d)
                        "G" -> stringResource(R.string.nota_info_g)
                        "B" -> stringResource(R.string.nota_info_b)
                        "C" -> stringResource(R.string.nota_info_c)
                        "F" -> stringResource(R.string.nota_info_f)
                        else -> stringResource(R.string.nota_info_default)
                    },
                    fontSize = 14.sp,
                    color = Color(0xFF3E5060)
                )

                Spacer(modifier = Modifier.height(16.dp))

                val opcionJonicos = stringResource(R.string.submenu_modos_jonicos)
                val opcionArmadura = stringResource(R.string.submenu_armadura)
                val opcionGriegos = stringResource(R.string.submenu_modos_griegos)

                // Contenido específico del submenu
                when (submenuActivo) {

                    opcionJonicos -> {
                        Text(
                            text = stringResource(R.string.descripcion_modos_jonicos),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { navController.navigate(Rutas.TEORIA_MODOS_JONICOS) }) {
                            Text(stringResource(R.string.ir_a_teoria))
                        }

                    }

                    opcionArmadura -> {
                        Text(
                            text = stringResource(R.string.descripcion_armadura),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { navController.navigate(Rutas.TEORIA_ARMADURA) }) {
                            Text(stringResource(R.string.ir_a_teoria))
                        }

                    }

                    opcionGriegos -> {
                        Text(
                            text = stringResource(R.string.descripcion_modos_griegos),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { navController.navigate(Rutas.TEORIA_MODOS_GRIEGOS) }) {
                            Text(stringResource(R.string.ir_a_teoria))
                        }

                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.padding(top = 16.dp)) {
                    RoscoProgreso(
                        porcentaje = porcentajeProgreso,
                        texto = stringResource(R.string.progreso_total_del_curso)
                    )
                }
            }
        }
    }
    LaunchedEffect(porcentajeProgreso) {
        //Esto fuerza el repintado si cambia el progreso
    }

}

@Composable
fun ShapePua(): Shape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val width = size.width
        val height = size.height

        val path = Path().apply {
            moveTo(width * 0.5f, 0f)
            quadraticTo(width, height * 0.2f, width * 0.8f, height * 0.8f)
            quadraticTo(width * 0.5f, height, width * 0.2f, height * 0.8f)
            quadraticTo(0f, height * 0.2f, width * 0.5f, 0f)
            close()
        }

        return Outline.Generic(path)
    }
}

@Composable
fun RoscoProgreso(porcentaje: Float, texto: String = "Progreso de esta actividad") {

    var progreso by remember { mutableStateOf(0f) }

    // Lanza la animación desde 0 hasta porcentajeFinal
    LaunchedEffect(porcentaje) {
        progreso = 0f
        delay(300) // pequeña pausa opcional
        progreso = porcentaje
    }

    val progresoAnimado by animateFloatAsState(
        targetValue = progreso,
        animationSpec = tween(durationMillis = 1000),
        label = "animacionRosco"
    )

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()

    ){
        Text(
            text = texto,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF153B59),
        )

        Spacer(modifier = Modifier.height(8.dp))


        CircularProgressIndicator(
            progress = { progresoAnimado },
            modifier = Modifier
                .size(100.dp),
            color = Color(0xFF1A6D1A),
            strokeWidth = 10.dp,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.porcentaje_completado, (porcentaje * 100).toInt()),
            fontSize = 14.sp,
            color = Color.DarkGray
        )

    }

}

//prueba:

@Composable
fun DemoRosco() {
    var progreso by remember { mutableStateOf(0f) }

    // Simula una carga
    LaunchedEffect(Unit) {
        delay(500)
        progreso = 0.75f // Esto dispara la animación
    }

    RoscoProgreso(porcentaje = progreso)
}


@Preview(showBackground = true)
@Composable
fun PreviewPantallaPrincipal() {
    val dummyNavController = rememberNavController()
    AppGuitarraTheme {
        PantallaPrincipal(navController = dummyNavController)
    }
}

