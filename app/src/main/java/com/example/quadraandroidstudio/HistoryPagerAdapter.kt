package com.example.quadraandroidstudio

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class HistoryPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2 // Tenemos dos pestañas: Pendientes y Hechas

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PendingReservationsFragment() // Fragment para reservas pendientes
            1 -> CompletedReservationsFragment() // Fragment para reservas completadas
            else -> throw IllegalStateException("Invalid position: $position")
        }
    }
}