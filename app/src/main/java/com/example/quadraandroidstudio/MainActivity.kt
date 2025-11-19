package com.example.quadraandroidstudio

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.quadraandroidstudio.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener el NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Configurar la BottomNavigationView con el NavController
        binding.bottomNavigationView.setupWithNavController(navController)

        handleIntentNavigation()

    }

    private fun handleIntentNavigation() {
        // Obtener el ID del destino del Intent, si existe
        val destinationId = intent.getIntExtra("NAVIGATE_TO_DESTINATION", 0)

        // Si recibimos un ID válido y NO es el destino actual (para evitar bucles)
        if (destinationId != 0 && navController.currentDestination?.id != destinationId) {
            // Navegar al destino solicitado
            navController.navigate(destinationId)

            // Limpiar el extra para que no vuelva a navegar si la Activity se recrea (ej. rotación)
            intent.removeExtra("NAVIGATE_TO_DESTINATION")
        }
    }

    override fun onNewIntent(intent: Intent) { // <-- REMOVED the question mark here
        super.onNewIntent(intent)
        setIntent(intent) // Actualiza el intent de la activity
        handleIntentNavigation() // Revisa si hay una nueva instrucción de navegación
    }
}