package com.tuequipo.autocare.ui.vehiculo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.tuequipo.autocare.domain.model.Vehiculo
import com.tuequipo.autocare.viewmodel.VehiculoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehiculosScreen(
    vehiculoViewModel: VehiculoViewModel,
    onVolver: () -> Unit
) {
    val vehiculos by vehiculoViewModel.vehiculos.collectAsState()
    val mensajeError by vehiculoViewModel.mensajeError.collectAsState()
    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var placa by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        vehiculoViewModel.cargarVehiculos()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Vehiculos") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(value = marca, onValueChange = { marca = it }, label = { Text("Marca") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = modelo, onValueChange = { modelo = it }, label = { Text("Modelo") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = placa, onValueChange = { placa = it }, label = { Text("Placa") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = tipo, onValueChange = { tipo = it }, label = { Text("Tipo") }, modifier = Modifier.fillMaxWidth())

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    enabled = marca.isNotBlank() && modelo.isNotBlank() && placa.isNotBlank() && tipo.isNotBlank(),
                    onClick = {
                        vehiculoViewModel.agregarVehiculo(
                            Vehiculo(
                                marca = marca,
                                modelo = modelo,
                                placa = placa,
                                tipoVehiculo = tipo
                            )
                        )
                        marca = ""
                        modelo = ""
                        placa = ""
                        tipo = ""
                    }
                ) {
                    Text("Registrar")
                }
                Button(onClick = onVolver) {
                    Text("Volver")
                }
            }

            mensajeError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(vehiculos) { vehiculo ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "${vehiculo.marca} ${vehiculo.modelo}", style = MaterialTheme.typography.titleMedium)
                            Text(text = "Placa: ${vehiculo.placa}")
                            Text(text = "Tipo: ${vehiculo.tipoVehiculo}")
                            Button(onClick = { vehiculoViewModel.eliminarVehiculo(vehiculo.idVehiculo) }) {
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}
