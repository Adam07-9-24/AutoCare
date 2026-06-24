package com.tuequipo.autocare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import com.tuequipo.autocare.data.local.database.AutoCareDatabase
import com.tuequipo.autocare.data.remote.api.RetrofitClient
import com.tuequipo.autocare.data.repository.AutoCareRepository
import com.tuequipo.autocare.ui.navigation.NavGraph
import com.tuequipo.autocare.ui.theme.AutoCareTheme
import com.tuequipo.autocare.viewmodel.AutoCareViewModelFactory
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
    private lateinit var viewModelFactory: AutoCareViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.rgb(11, 17, 23)),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.rgb(11, 17, 23))
        )

        val database = AutoCareDatabase.getDatabase(applicationContext)
        val repository = AutoCareRepository(
            vehiculoDao = database.vehiculoDao(),
            mantenimientoDao = database.mantenimientoDao(),
            carApiService = RetrofitClient.carApiService
        )
        viewModelFactory = AutoCareViewModelFactory(repository)

        setContent {
            AutoCareTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(factory = viewModelFactory)
                }
            }
        }
    }
}
