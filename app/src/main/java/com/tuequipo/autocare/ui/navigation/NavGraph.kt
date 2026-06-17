package com.tuequipo.autocare.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tuequipo.autocare.ui.mantenimiento.DetalleScreen
import com.tuequipo.autocare.ui.mantenimiento.FormularioScreen
import com.tuequipo.autocare.ui.mantenimiento.ListaScreen
import com.tuequipo.autocare.ui.resumen.ResumenScreen
import com.tuequipo.autocare.ui.vehiculo.VehiculosScreen
import com.tuequipo.autocare.viewmodel.AutoCareViewModelFactory
import com.tuequipo.autocare.viewmodel.MantenimientoViewModel
import com.tuequipo.autocare.viewmodel.VehiculoViewModel

object Routes {
    const val ListaMantenimientos = "listaMantenimientos"
    const val DetalleMantenimiento = "detalleMantenimiento/{idMantenimiento}"
    const val NuevoMantenimiento = "nuevoMantenimiento"
    const val EditarMantenimiento = "editarMantenimiento/{idMantenimiento}"
    const val Vehiculos = "vehiculos"
    const val Resumen = "resumen"

    fun detalleMantenimiento(idMantenimiento: Int) = "detalleMantenimiento/$idMantenimiento"
    fun editarMantenimiento(idMantenimiento: Int) = "editarMantenimiento/$idMantenimiento"
}

@Composable
fun NavGraph(factory: AutoCareViewModelFactory) {
    val navController = rememberNavController()
    val mantenimientoViewModel: MantenimientoViewModel = viewModel(factory = factory)
    val vehiculoViewModel: VehiculoViewModel = viewModel(factory = factory)

    NavHost(
        navController = navController,
        startDestination = Routes.ListaMantenimientos
    ) {
        composable(Routes.ListaMantenimientos) {
            ListaScreen(
                mantenimientoViewModel = mantenimientoViewModel,
                onNuevo = { navController.navigate(Routes.NuevoMantenimiento) },
                onDetalle = { id -> navController.navigate(Routes.detalleMantenimiento(id)) },
                onVehiculos = { navController.navigate(Routes.Vehiculos) },
                onResumen = { navController.navigate(Routes.Resumen) }
            )
        }

        composable(
            route = Routes.DetalleMantenimiento,
            arguments = listOf(navArgument("idMantenimiento") { type = NavType.IntType })
        ) { backStackEntry ->
            val idMantenimiento = backStackEntry.arguments?.getInt("idMantenimiento") ?: 0
            DetalleScreen(
                idMantenimiento = idMantenimiento,
                mantenimientoViewModel = mantenimientoViewModel,
                vehiculoViewModel = vehiculoViewModel,
                onEditar = { navController.navigate(Routes.editarMantenimiento(idMantenimiento)) },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Routes.NuevoMantenimiento) {
            FormularioScreen(
                idMantenimiento = null,
                mantenimientoViewModel = mantenimientoViewModel,
                vehiculoViewModel = vehiculoViewModel,
                onGuardar = { navController.popBackStack() },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.EditarMantenimiento,
            arguments = listOf(navArgument("idMantenimiento") { type = NavType.IntType })
        ) { backStackEntry ->
            val idMantenimiento = backStackEntry.arguments?.getInt("idMantenimiento") ?: 0
            FormularioScreen(
                idMantenimiento = idMantenimiento,
                mantenimientoViewModel = mantenimientoViewModel,
                vehiculoViewModel = vehiculoViewModel,
                onGuardar = { navController.popBackStack() },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Routes.Vehiculos) {
            VehiculosScreen(
                vehiculoViewModel = vehiculoViewModel,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Routes.Resumen) {
            ResumenScreen(
                mantenimientoViewModel = mantenimientoViewModel,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
