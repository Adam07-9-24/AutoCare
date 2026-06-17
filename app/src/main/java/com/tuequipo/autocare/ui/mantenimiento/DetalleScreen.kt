package com.tuequipo.autocare.ui.mantenimiento

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
        topBar = { TopAppBar(title = { Text("Detalle") }) }
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
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = mantenimiento.titulo, style = MaterialTheme.typography.titleLarge)
                        Text(text = "Descripcion: ${mantenimiento.descripcion}")
                        Text(text = "Tipo: ${mantenimiento.tipoMantenimiento}")
                        Text(text = "Fecha programada: ${mantenimiento.fechaProgramada}")
                        Text(text = "Estado: ${mantenimiento.estado}")
                        Text(text = "Vehiculo: ${vehiculo?.let { "${it.marca} ${it.modelo} - ${it.placa}" } ?: "No disponible"}")
                        Text(text = "Recordatorio: ${if (mantenimiento.recordatorioActivo) "Activo" else "Inactivo"}")
                    }
                }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Informacion tecnica", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = mantenimientoState.consejoApi ?: "Sin informacion tecnica disponible.")
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onEditar) {
                        Text("Editar")
                    }
                    Button(
                        onClick = {
                            mantenimientoViewModel.eliminarMantenimiento(idMantenimiento)
                            onVolver()
                        }
                    ) {
                        Text("Eliminar")
                    }
                    Button(onClick = onVolver) {
                        Text("Volver")
                    }
                }
            } else if (!mantenimientoState.isLoading) {
                Text("No se encontro el mantenimiento.")
                Button(onClick = onVolver) {
                    Text("Volver")
                }
            }
        }
    }
}
