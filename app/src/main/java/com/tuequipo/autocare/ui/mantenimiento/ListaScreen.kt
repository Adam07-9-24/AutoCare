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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.tuequipo.autocare.viewmodel.MantenimientoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(
    mantenimientoViewModel: MantenimientoViewModel,
    onNuevo: () -> Unit,
    onDetalle: (Int) -> Unit,
    onVehiculos: () -> Unit,
    onResumen: () -> Unit
) {
    val uiState by mantenimientoViewModel.uiState.collectAsState()
    var filtro by remember { mutableStateOf("Todos") }
    val estados = listOf("Todos", "Pendiente", "Realizado", "Vencido")
    val mantenimientos = if (filtro == "Todos") {
        uiState.mantenimientos
    } else {
        uiState.mantenimientos.filter { it.estado.equals(filtro, ignoreCase = true) }
    }

    LaunchedEffect(Unit) {
        mantenimientoViewModel.cargarMantenimientos()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mantenimientos") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNuevo) {
                Text("+")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onVehiculos) {
                    Text("Vehiculos")
                }
                Button(onClick = onResumen) {
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

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(mantenimientos) { mantenimiento ->
                    MantenimientoCard(
                        mantenimiento = mantenimiento,
                        onClick = { onDetalle(mantenimiento.idMantenimiento) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MantenimientoCard(
    mantenimiento: Mantenimiento,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = mantenimiento.titulo, style = MaterialTheme.typography.titleMedium)
            Text(text = "Tipo: ${mantenimiento.tipoMantenimiento}")
            Text(text = "Fecha: ${mantenimiento.fechaProgramada}")
            Text(text = "Estado: ${mantenimiento.estado}")
        }
    }
}
