package com.example.quadraandroidstudio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quadraandroidstudio.databinding.FragmentHistoryBinding // Asegúrate que el binding se genere correctamente
import com.google.android.material.tabs.TabLayoutMediator

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar el ViewPager2 con el adaptador de pestañas
        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager

        // Se necesita un adaptador que maneje los Fragments para cada pestaña
        // Este adaptador lo crearemos en el siguiente paso
        val pagerAdapter = HistoryPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        // Conectar TabLayout con ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Pendientes"
                1 -> "Hechas"
                else -> ""
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}