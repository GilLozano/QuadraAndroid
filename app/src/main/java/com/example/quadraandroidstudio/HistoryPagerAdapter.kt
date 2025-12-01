package com.example.quadraandroidstudio

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class HistoryPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2 // Tenemos 2 pestañas

    override fun createFragment(position: Int): Fragment {
        // Posición 0 = Pestaña izquierda (Pendientes)
        // Posición 1 = Pestaña derecha (Hechas)
        return when (position) {
            0 -> ActiveReservationsFragment()
            1 -> PastReservationsFragment()
            else -> throw IllegalArgumentException("Posición inválida")
        }
    }
}