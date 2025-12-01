package com.example.quadraandroidstudio

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.quadraandroidstudio.databinding.FragmentHistoryBinding
import com.example.quadraandroidstudio.utils.SharedPreferencesManager
import com.google.android.material.tabs.TabLayoutMediator

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPagerAndTabs()
    }

    private fun setupViewPagerAndTabs() {
        // 1. Conectar el adaptador al ViewPager2
        val pagerAdapter = HistoryPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        // 2. Conectar el TabLayout con el ViewPager2 usando un Mediator
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            // Asignar títulos a las pestañas según la posición
            tab.text = when (position) {
                0 -> "Pendientes"
                1 -> "Hechas"
                else -> null
            }
        }.attach()
    }

    override fun onResume() {
        super.onResume()
        // Cada vez que el fragmento se vuelve visible (al entrar por primera vez
        // o al volver de editar), recargamos los datos frescos del servidor.
        Log.d("HistoryFragment", "onResume: Recargando datos...")
        loadData()
    }

    private fun loadData() {
        // El fragmento padre se encarga de pedir los datos una vez
        val userId = SharedPreferencesManager.getUserId(requireContext())
        if (userId != -1) {
            sharedViewModel.fetchUserReservations(userId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Es importante liberar el binding del ViewPager para evitar fugas de memoria
        binding.viewPager.adapter = null
        _binding = null
    }
}