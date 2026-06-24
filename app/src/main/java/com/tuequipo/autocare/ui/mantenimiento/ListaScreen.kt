package com.tuequipo.autocare.ui.mantenimiento

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tuequipo.autocare.domain.model.Mantenimiento
import com.tuequipo.autocare.ui.common.HeaderGradient
import com.tuequipo.autocare.ui.common.FilterChipAutoCare
import com.tuequipo.autocare.ui.common.GradientFloatingActionButton
import com.tuequipo.autocare.ui.common.StatusChip
import com.tuequipo.autocare.ui.common.estadoColor
import com.tuequipo.autocare.ui.common.formatFecha
import com.tuequipo.autocare.ui.common.ShimmerBox
import com.tuequipo.autocare.ui.common.iconoTipoMantenimiento
import com.tuequipo.autocare.ui.common.pressScale
import com.tuequipo.autocare.viewmodel.MantenimientoViewModel
import com.tuequipo.autocare.viewmodel.VehiculoViewModel
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(
    viewModel: MantenimientoViewModel,
    vehiculoViewModel: VehiculoViewModel,
    onNavigateToDetalle: (Int) -> Unit,
    onNavigateToFormulario: (Int?) -> Unit,
    onNavigateToVehiculos: () -> Unit,
    onNavigateToResumen: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val vehiculos by vehiculoViewModel.vehiculos.collectAsState()
    var filtro by remember { mutableStateOf("Todos") }
    val filtros = listOf("Todos", "Pendiente", "Realizado", "Vencido")
    val lista = if (filtro == "Todos") uiState.mantenimientos
    else uiState.mantenimientos.filter { it.estado == filtro }

    val pendientes = uiState.mantenimientos.count { it.estado == "Pendiente" }
    val vencidos = uiState.mantenimientos.count { it.estado == "Vencido" }
    val saludo = remember {
        when (LocalTime.now().hour) {
            in 5..11 -> "¡Buenos días!"
            in 12..18 -> "¡Buenas tardes!"
            else -> "¡Buenas noches!"
        }
    }

    val haptics = LocalHapticFeedback.current
    val fabInteraction = remember { MutableInteractionSource() }

    Scaffold(
        floatingActionButton = {
            GradientFloatingActionButton(
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onNavigateToFormulario(null)
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Hero de bienvenida
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(HeaderGradient)
                        .heightIn(min = 270.dp)
                        .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    saludo,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                                )
                                Text(
                                    "Tu Auto Care",
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Text(
                                    "${uiState.mantenimientos.size} mantenimientos registrados",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            HeroPill(Icons.Default.Schedule, "$pendientes pendientes")
                            HeroPill(Icons.Default.Warning, "$vencidos vencidos")
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            QuickActionCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.DirectionsCar,
                                label = "Tus Vehículos",
                                subtitle = "${vehiculos.size} registrados",
                                onClick = onNavigateToVehiculos
                            )
                            QuickActionCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.BarChart,
                                label = "Tu Resumen",
                                onClick = onNavigateToResumen
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    "Tus Mantenimientos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                )
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filtros) { f ->
                        FilterChipAutoCare(
                            label = f,
                            selected = filtro == f,
                            onClick = { filtro = f }
                        )
                    }
                }
            }

            when {
                uiState.isLoading -> items(3) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 5.dp)
                            .height(86.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ShimmerBox(modifier = Modifier.fillMaxWidth(0.6f).height(16.dp))
                        ShimmerBox(modifier = Modifier.fillMaxWidth(0.4f).height(12.dp))
                    }
                }

                lista.isEmpty() -> item { EmptyListState() }

                else -> itemsIndexed(lista, key = { _, m -> m.idMantenimiento }) { index, m ->
                    var visible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) { visible = true }
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(250, delayMillis = index * 40)) +
                                slideInVertically(
                                    animationSpec = tween(250, delayMillis = index * 40),
                                    initialOffsetY = { it / 4 }
                                )
                    ) {
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                            val vehiculo = vehiculos.find { it.idVehiculo == m.idVehiculo }
                            MantenimientoListCard(
                                m = m,
                                vehiculoTexto = vehiculo?.let { "${it.marca} ${it.modelo}" },
                                onClick = { onNavigateToDetalle(m.idMantenimiento) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroPill(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.16f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onPrimary)
        Text(text, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimary)
    }
}

@Composable
private fun QuickActionCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val interaction = remember { MutableInteractionSource() }
    Card(
        onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier.pressScale(interaction),
        interactionSource = interaction,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.16f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.24f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                if (subtitle != null) {
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
                    )
                }
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

@Composable
private fun EmptyListState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            Text(
                "Sin mantenimientos",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                "Pulsa + para agregar el primero",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
fun MantenimientoListCard(m: Mantenimiento, vehiculoTexto: String? = null, onClick: () -> Unit) {
    val statusColor = estadoColor(m.estado)
    val haptics = LocalHapticFeedback.current
    val interaction = remember { MutableInteractionSource() }
    Card(
        onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .pressScale(interaction),
        interactionSource = interaction,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(statusColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconoTipoMantenimiento(m.tipoMantenimiento),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = statusColor
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = m.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    )
                    StatusChip(m.estado)
                }
                if (!vehiculoTexto.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                        )
                        Text(
                            text = vehiculoTexto,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                    )
                    Text(
                        text = formatFecha(m.fechaProgramada),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                    )
                    Text(
                        text = "·",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                    Text(
                        text = m.tipoMantenimiento,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = statusColor
                    )
                }
            }
        }
    }
}
