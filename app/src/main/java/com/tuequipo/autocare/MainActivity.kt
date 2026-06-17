package com.tuequipo.autocare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tuequipo.autocare.data.local.database.AutoCareDatabase
import com.tuequipo.autocare.data.remote.api.RetrofitClient
import com.tuequipo.autocare.data.repository.AutoCareRepository
import com.tuequipo.autocare.ui.navigation.NavGraph
import com.tuequipo.autocare.ui.theme.AutoCareTheme
import com.tuequipo.autocare.viewmodel.AutoCareViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var viewModelFactory: AutoCareViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AutoCareDatabase.getDatabase(applicationContext)
        val repository = AutoCareRepository(
            vehiculoDao = database.vehiculoDao(),
            mantenimientoDao = database.mantenimientoDao(),
            carApiService = RetrofitClient.carApiService
        )
        viewModelFactory = AutoCareViewModelFactory(repository)

        setContent {
            AutoCareTheme {
                NavGraph(factory = viewModelFactory)
            }
        }
    }
}
