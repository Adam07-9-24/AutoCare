package com.tuequipo.autocare.ui.resumen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.tuequipo.autocare.domain.model.Mantenimiento
import com.tuequipo.autocare.viewmodel.MantenimientoViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenScreen(
    mantenimientoViewModel: MantenimientoViewModel,
    onVolver: () -> Unit
) {
    val uiState by mantenimientoViewModel.uiState.collectAsState()
    val mantenimientos = uiState.mantenimientos

    LaunchedEffect(Unit) {
        mantenimientoViewModel.cargarMantenimientos()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Resumen") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ResumenCard("Total", mantenimientos.size.toString(), Modifier.weight(1f))
                ResumenCard("Pendientes", contarPorEstado(mantenimientos, "Pendiente").toString(), Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ResumenCard("Realizados", contarPorEstado(mantenimientos, "Realizado").toString(), Modifier.weight(1f))
                ResumenCard("Vencidos", contarPorEstado(mantenimientos, "Vencido").toString(), Modifier.weight(1f))
            }
            ResumenCard("Proximos", contarProximos(mantenimientos).toString(), Modifier.fillMaxWidth())

            Button(onClick = onVolver) {
                Text("Volver")
            }
        }
    }
}

@Composable
private fun ResumenCard(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = titulo, style = MaterialTheme.typography.titleMedium)
            Text(text = valor, style = MaterialTheme.typography.headlineMedium)
        }
    }
}

private fun contarPorEstado(mantenimientos: List<Mantenimiento>, estado: String): Int {
    return mantenimientos.count { it.estado.equals(estado, ignoreCase = true) }
}

private fun contarProximos(mantenimientos: List<Mantenimiento>): Int {
    val hoy = LocalDate.now()
    val limite = hoy.plusDays(7)
    return mantenimientos.count { mantenimiento ->
        val fecha = runCatching { LocalDate.parse(mantenimiento.fechaProgramada) }.getOrNull()
        fecha != null && !fecha.isBefore(hoy) && !fecha.isAfter(limite)
    }
}
