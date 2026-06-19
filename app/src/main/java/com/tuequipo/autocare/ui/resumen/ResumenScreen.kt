package com.tuequipo.autocare.ui.resumen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        topBar = {
            TopAppBar(
                title = { Text("Resumen") },
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
            Text(
                text = "Estadisticas de mantenimientos",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ResumenCard("Pendientes", contarPorEstado(mantenimientos, "Pendiente").toString(), Color(0xFFE3ECF8), Modifier.weight(1f))
                ResumenCard("Realizados", contarPorEstado(mantenimientos, "Realizado").toString(), Color(0xFFDFF3E6), Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ResumenCard("Vencidos", contarPorEstado(mantenimientos, "Vencido").toString(), Color(0xFFFFE0E0), Modifier.weight(1f))
                ResumenCard("Proximos 30 dias", contarProximos(mantenimientos).toString(), Color(0xFFFFF3D6), Modifier.weight(1f))
            }

        }
    }
}

@Composable
private fun ResumenCard(
    titulo: String,
    valor: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = titulo, style = MaterialTheme.typography.titleSmall)
            Text(
                text = valor,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun contarPorEstado(mantenimientos: List<Mantenimiento>, estado: String): Int {
    return mantenimientos.count { it.estado.equals(estado, ignoreCase = true) }
}

private fun contarProximos(mantenimientos: List<Mantenimiento>): Int {
    val hoy = LocalDate.now()
    val limite = hoy.plusDays(30)
    return mantenimientos.count { mantenimiento ->
        val fecha = runCatching { LocalDate.parse(mantenimiento.fechaProgramada) }.getOrNull()
        fecha != null && !fecha.isBefore(hoy) && !fecha.isAfter(limite)
    }
}
