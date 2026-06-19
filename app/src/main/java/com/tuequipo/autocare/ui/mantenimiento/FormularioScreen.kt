package com.tuequipo.autocare.ui.mantenimiento

import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

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
    var mostrarVehiculos by remember { mutableStateOf(false) }
    var mostrarTipos by remember { mutableStateOf(false) }
    var mostrarEstados by remember { mutableStateOf(false) }
    var mostrarFecha by remember { mutableStateOf(false) }
    val tipos = listOf("Preventivo", "Correctivo", "Revision", "Cambio")
    val estados = listOf("Pendiente", "Realizado", "Vencido")

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
        topBar = {
            TopAppBar(
                title = { Text(if (editando) "Editar mantenimiento" else "Nuevo mantenimiento") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Datos del mantenimiento",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    SelectorField(
                        label = "Vehículo",
                        value = vehiculoSeleccionado?.let { "${it.marca} ${it.modelo} - ${it.placa}" } ?: "Seleccionar vehículo",
                        onClick = { mostrarVehiculos = true }
                    )

                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Titulo") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripcion") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    SelectorField(
                        label = "Tipo de mantenimiento",
                        value = tipo,
                        onClick = { mostrarTipos = true }
                    )

                    SelectorField(
                        label = "Fecha programada",
                        value = fecha.ifBlank { "Seleccionar fecha" },
                        onClick = { mostrarFecha = true }
                    )

                    SelectorField(
                        label = "Estado",
                        value = estado,
                        onClick = { mostrarEstados = true }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Recordatorio", style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = if (recordatorio) "Activo" else "Inactivo",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(checked = recordatorio, onCheckedChange = { recordatorio = it })
                    }
                }
            }

            mantenimientoState.mensajeError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                enabled = vehiculoSeleccionado != null && titulo.isNotBlank() && fecha.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
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
        }
    }

    if (mostrarFecha) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = fecha.toDatePickerMillis()
        )

        DatePickerDialog(
            onDismissRequest = { mostrarFecha = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            fecha = it.toDateText()
                        }
                        mostrarFecha = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarFecha = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (mostrarVehiculos) {
        ModalBottomSheet(onDismissRequest = { mostrarVehiculos = false }) {
            BottomSheetTitle("Seleccionar vehículo")
            if (vehiculos.isEmpty()) {
                Text(
                    text = "No hay vehículos registrados",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    vehiculos.forEach { vehiculo ->
                        VehiculoOptionCard(
                            vehiculo = vehiculo,
                            seleccionado = vehiculoSeleccionado?.idVehiculo == vehiculo.idVehiculo,
                            onClick = {
                                vehiculoSeleccionado = vehiculo
                                mostrarVehiculos = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (mostrarTipos) {
        OpcionesBottomSheet(
            titulo = "Seleccionar tipo",
            opciones = tipos,
            seleccionado = tipo,
            onSeleccionar = {
                tipo = it
                mostrarTipos = false
            },
            onCerrar = { mostrarTipos = false }
        )
    }

    if (mostrarEstados) {
        OpcionesBottomSheet(
            titulo = "Seleccionar estado",
            opciones = estados,
            seleccionado = estado,
            onSeleccionar = {
                estado = it
                mostrarEstados = false
            },
            onCerrar = { mostrarEstados = false }
        )
    }
}

@Composable
private fun SelectorField(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.titleSmall)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
        ) {
            Text(
                text = value,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun VehiculoOptionCard(
    vehiculo: Vehiculo,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionado) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "${vehiculo.marca} ${vehiculo.modelo}",
                style = MaterialTheme.typography.titleSmall,
                color = if (seleccionado) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Placa: ${vehiculo.placa}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun OpcionesBottomSheet(
    titulo: String,
    opciones: List<String>,
    seleccionado: String,
    onSeleccionar: (String) -> Unit,
    onCerrar: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onCerrar) {
        BottomSheetTitle(titulo)
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            opciones.forEach { opcion ->
                OpcionCard(
                    text = opcion,
                    selected = seleccionado == opcion,
                    onClick = { onSeleccionar(opcion) }
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun OpcionCard(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
            }
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun BottomSheetTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary
    )
}

private fun String.toDatePickerMillis(): Long? {
    return runCatching {
        LocalDate.parse(this)
            .atStartOfDay()
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli()
    }.getOrNull()
}

private fun Long.toDateText(): String {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
        .toString()
}
