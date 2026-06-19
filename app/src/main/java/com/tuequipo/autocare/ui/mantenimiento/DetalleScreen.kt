package com.tuequipo.autocare.ui.mantenimiento

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tuequipo.autocare.viewmodel.MantenimientoViewModel
import com.tuequipo.autocare.viewmodel.VehiculoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    idMantenimiento: Int,
    mantenimientoViewModel: MantenimientoViewModel,
    vehiculoViewModel: VehiculoViewModel,
    onEditar: () -> Unit,
    onVolver: () -> Unit
) {
    val mantenimientoState by mantenimientoViewModel.uiState.collectAsState()
    val vehiculos by vehiculoViewModel.vehiculos.collectAsState()
    val mantenimiento = mantenimientoState.mantenimientoSeleccionado
    val vehiculo = vehiculos.find { it.idVehiculo == mantenimiento?.idVehiculo }
    var mostrarConfirmacion by remember { mutableStateOf(false) }

    LaunchedEffect(idMantenimiento) {
        mantenimientoViewModel.cargarDetalle(idMantenimiento)
        vehiculoViewModel.cargarVehiculos()
    }

    LaunchedEffect(vehiculo?.idVehiculo) {
        if (vehiculo != null) {
            mantenimientoViewModel.cargarDatosTecnicos(vehiculo.marca, vehiculo.modelo)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle") },
                navigationIcon = {
                    IconButton(
                        onClick = onVolver,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (mantenimientoState.isLoading) {
                Text("Cargando...")
            }

            mantenimientoState.mensajeError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            if (mantenimiento != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = mantenimiento.titulo,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.weight(1f)
                            )
                            EstadoChip(estado = mantenimiento.estado)
                        }
                        DetailLine("Descripcion", mantenimiento.descripcion.ifBlank { "Sin descripcion" })
                        DetailLine("Fecha programada", mantenimiento.fechaProgramada)
                        DetailLine("Tipo", mantenimiento.tipoMantenimiento)
                        DetailLine("Recordatorio", if (mantenimiento.recordatorioActivo) "Activo" else "Inactivo")
                        DetailLine("Vehiculo", vehiculo?.let { "${it.marca} ${it.modelo} - ${it.placa}" } ?: "No disponible")
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Datos técnicos del vehículo",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        val textoTecnico = when {
                            mantenimientoState.isLoadingDatosTecnicos -> "Cargando información técnica..."
                            mantenimientoState.consejoApi != null -> mantenimientoState.consejoApi.orEmpty()
                            else -> "No se encontró información técnica para este modelo."
                        }
                        TechnicalInfo(text = textoTecnico)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = onEditar, modifier = Modifier.weight(1f)) {
                        Text("Editar")
                    }
                    Button(
                        onClick = { mostrarConfirmacion = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Eliminar")
                    }
                }
            } else if (!mantenimientoState.isLoading) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("No se encontro el mantenimiento.")
                    }
                }
            }
        }
    }

    if (mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacion = false },
            title = { Text("Eliminar mantenimiento") },
            text = { Text("Esta accion eliminara el mantenimiento seleccionado.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacion = false
                        mantenimientoViewModel.eliminarMantenimiento(idMantenimiento)
                        onVolver()
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacion = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun TechnicalInfo(text: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        text.lines()
            .filter { it.isNotBlank() }
            .forEach { line ->
                val parts = line.split(":", limit = 2)
                if (parts.size == 2) {
                    TechnicalLine(label = parts[0].trim(), value = parts[1].trim())
                } else {
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
    }
}

@Composable
private fun TechnicalLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun EstadoChip(estado: String) {
    val container = when (estado.lowercase()) {
        "realizado" -> Color(0xFFDFF3E6)
        "vencido" -> Color(0xFFFFE0E0)
        else -> Color(0xFFE3ECF8)
    }
    val content = when (estado.lowercase()) {
        "realizado" -> Color(0xFF1B6B3A)
        "vencido" -> Color(0xFF9B1C1C)
        else -> Color(0xFF214C7A)
    }
    Surface(
        shape = RoundedCornerShape(50),
        color = container,
        contentColor = content
    ) {
        Text(
            text = estado,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}
