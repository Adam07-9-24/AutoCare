package com.tuequipo.autocare.ui.mantenimiento

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tuequipo.autocare.domain.model.Mantenimiento
import com.tuequipo.autocare.domain.model.Vehiculo
import com.tuequipo.autocare.viewmodel.MantenimientoViewModel
import com.tuequipo.autocare.viewmodel.VehiculoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(
    mantenimientoViewModel: MantenimientoViewModel,
    vehiculoViewModel: VehiculoViewModel,
    onNuevo: () -> Unit,
    onDetalle: (Int) -> Unit,
    onVehiculos: () -> Unit,
    onResumen: () -> Unit
) {
    val uiState by mantenimientoViewModel.uiState.collectAsState()
    val vehiculos by vehiculoViewModel.vehiculos.collectAsState()
    var filtro by remember { mutableStateOf("Todos") }
    val estados = listOf("Todos", "Pendiente", "Realizado", "Vencido")
    val mantenimientos = if (filtro == "Todos") {
        uiState.mantenimientos
    } else {
        uiState.mantenimientos.filter { it.estado.equals(filtro, ignoreCase = true) }
    }

    LaunchedEffect(Unit) {
        mantenimientoViewModel.cargarMantenimientos()
        vehiculoViewModel.cargarVehiculos()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mantenimientos") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNuevo) {
                Text("+", style = MaterialTheme.typography.titleLarge)
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onVehiculos, modifier = Modifier.weight(1f)) {
                    Text("Vehiculos")
                }
                Button(onClick = onResumen, modifier = Modifier.weight(1f)) {
                    Text("Resumen")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                estados.forEach { estado ->
                    FilterChip(
                        selected = filtro == estado,
                        onClick = { filtro = estado },
                        label = { Text(estado) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.isLoading) {
                Text("Cargando mantenimientos...")
            }

            uiState.mensajeError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            if (!uiState.isLoading && mantenimientos.isEmpty()) {
                EmptyMessage(text = "No hay mantenimientos para mostrar.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(mantenimientos) { mantenimiento ->
                        val vehiculo = vehiculos.find { it.idVehiculo == mantenimiento.idVehiculo }
                        MantenimientoCard(
                            mantenimiento = mantenimiento,
                            vehiculo = vehiculo,
                            onClick = { onDetalle(mantenimiento.idMantenimiento) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MantenimientoCard(
    mantenimiento: Mantenimiento,
    vehiculo: Vehiculo?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = mantenimiento.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                EstadoChip(estado = mantenimiento.estado)
            }
            Text(
                text = mantenimiento.descripcion.ifBlank { "Sin descripcion" },
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(text = "Fecha: ${mantenimiento.fechaProgramada}")
            Text(text = "Vehiculo: ${vehiculo?.let { "${it.marca} ${it.modelo} - ${it.placa}" } ?: "No disponible"}")
        }
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

@Composable
private fun EmptyMessage(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(20.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
