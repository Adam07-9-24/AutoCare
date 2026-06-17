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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.unit.dp
import com.tuequipo.autocare.domain.model.Mantenimiento
import com.tuequipo.autocare.domain.model.Vehiculo
import com.tuequipo.autocare.viewmodel.MantenimientoViewModel
import com.tuequipo.autocare.viewmodel.VehiculoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen(
    idMantenimiento: Int?,
    mantenimientoViewModel: MantenimientoViewModel,
    vehiculoViewModel: VehiculoViewModel,
    onGuardar: () -> Unit,
    onVolver: () -> Unit
) {
    val mantenimientoState by mantenimientoViewModel.uiState.collectAsState()
    val vehiculos by vehiculoViewModel.vehiculos.collectAsState()
    val mantenimientoActual = mantenimientoState.mantenimientoSeleccionado
    val editando = idMantenimiento != null

    var vehiculoSeleccionado by remember { mutableStateOf<Vehiculo?>(null) }
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("Preventivo") }
    var fecha by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("Pendiente") }
    var recordatorio by remember { mutableStateOf(false) }
    var menuVehiculos by remember { mutableStateOf(false) }
    var menuTipos by remember { mutableStateOf(false) }
    var menuEstados by remember { mutableStateOf(false) }

    LaunchedEffect(idMantenimiento) {
        vehiculoViewModel.cargarVehiculos()
        if (idMantenimiento != null) {
            mantenimientoViewModel.cargarDetalle(idMantenimiento)
        }
    }

    LaunchedEffect(mantenimientoActual, vehiculos) {
        if (editando && mantenimientoActual != null) {
            vehiculoSeleccionado = vehiculos.find { it.idVehiculo == mantenimientoActual.idVehiculo }
            titulo = mantenimientoActual.titulo
            descripcion = mantenimientoActual.descripcion
            tipo = mantenimientoActual.tipoMantenimiento
            fecha = mantenimientoActual.fechaProgramada
            estado = mantenimientoActual.estado
            recordatorio = mantenimientoActual.recordatorioActivo
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(if (editando) "Editar mantenimiento" else "Nuevo mantenimiento") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(onClick = { menuVehiculos = true }) {
                Text(vehiculoSeleccionado?.let { "${it.marca} ${it.modelo} - ${it.placa}" } ?: "Seleccionar vehiculo")
            }
            DropdownMenu(expanded = menuVehiculos, onDismissRequest = { menuVehiculos = false }) {
                vehiculos.forEach { vehiculo ->
                    DropdownMenuItem(
                        text = { Text("${vehiculo.marca} ${vehiculo.modelo} - ${vehiculo.placa}") },
                        onClick = {
                            vehiculoSeleccionado = vehiculo
                            menuVehiculos = false
                        }
                    )
                }
            }

            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Titulo") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripcion") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = { menuTipos = true }) {
                Text("Tipo: $tipo")
            }
            DropdownMenu(expanded = menuTipos, onDismissRequest = { menuTipos = false }) {
                listOf("Preventivo", "Correctivo", "Revision", "Cambio").forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            tipo = opcion
                            menuTipos = false
                        }
                    )
                }
            }

            OutlinedTextField(
                value = fecha,
                onValueChange = { fecha = it },
                label = { Text("Fecha programada") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = { menuEstados = true }) {
                Text("Estado: $estado")
            }
            DropdownMenu(expanded = menuEstados, onDismissRequest = { menuEstados = false }) {
                listOf("Pendiente", "Realizado", "Vencido").forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            estado = opcion
                            menuEstados = false
                        }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Recordatorio")
                Switch(checked = recordatorio, onCheckedChange = { recordatorio = it })
            }

            mantenimientoState.mensajeError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    enabled = vehiculoSeleccionado != null && titulo.isNotBlank(),
                    onClick = {
                        val mantenimiento = Mantenimiento(
                            idMantenimiento = idMantenimiento ?: 0,
                            idVehiculo = vehiculoSeleccionado?.idVehiculo ?: 0,
                            titulo = titulo,
                            descripcion = descripcion,
                            tipoMantenimiento = tipo,
                            fechaProgramada = fecha,
                            estado = estado,
                            recordatorioActivo = recordatorio
                        )
                        if (editando) {
                            mantenimientoViewModel.editarMantenimiento(mantenimiento)
                        } else {
                            mantenimientoViewModel.guardarMantenimiento(mantenimiento)
                        }
                        onGuardar()
                    }
                ) {
                    Text("Guardar")
                }
                Button(onClick = onVolver) {
                    Text("Volver")
                }
            }
        }
    }
}
