package com.example.quadraandroidstudio.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quadraandroidstudio.R
import com.example.quadraandroidstudio.databinding.ItemReservationBinding
import com.example.quadraandroidstudio.model.Reservation

class ReservationAdapter(private val onClick: (Reservation) -> Unit) :
    ListAdapter<Reservation, ReservationAdapter.ReservationViewHolder>(ReservationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val binding = ItemReservationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReservationViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        val reservation = getItem(position)
        holder.bind(reservation)
    }

    inner class ReservationViewHolder(
        private val binding: ItemReservationBinding,
        private val onClick: (Reservation) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(reservation: Reservation) {
            binding.tvCarName.text = reservation.car.name
            binding.tvCarBrandYear.text = "Marca: ${reservation.car.brand} - Año: ${reservation.car.year}"
            binding.tvCarPrice.text = "Precio: $${reservation.car.pricePerDay}/día"

            // Cargar imagen del coche usando el nombre del drawable
            val imageResId = binding.root.context.resources.getIdentifier(
                reservation.imageUrl, "drawable", binding.root.context.packageName
            )
            if (imageResId != 0) {
                binding.ivCarImage.setImageResource(imageResId)
            } else {
                // Si la imagen no se encuentra, puedes poner una por defecto o loggear un error
                binding.ivCarImage.setImageResource(R.drawable.ic_car) // Icono por defecto
            }

            // Configurar el tag de estado
            binding.tvStatusTag.text = reservation.status
            when (reservation.status) {
                "PENDIENTE" -> {
                    binding.tvStatusTag.background = ContextCompat.getDrawable(binding.root.context, R.drawable.rounded_background_pending)
                    binding.btnAction.text = "GESTIONAR"
                    binding.btnAction.setTextColor(ContextCompat.getColor(binding.root.context, R.color.colorPrimary))
                    binding.btnAction.strokeColor = ContextCompat.getColorStateList(binding.root.context, R.color.colorPrimary)
                }
                "COMPLETADA" -> {
                    binding.tvStatusTag.background = ContextCompat.getDrawable(binding.root.context, R.drawable.rounded_background_completed)
                    binding.btnAction.text = "VER DETALLES"
                    binding.btnAction.setTextColor(ContextCompat.getColor(binding.root.context, R.color.colorTextSecondary))
                    binding.btnAction.strokeColor = ContextCompat.getColorStateList(binding.root.context, R.color.colorTextSecondary)
                }
                else -> {
                    // Estado por defecto o manejar otros estados
                    binding.tvStatusTag.background = ContextCompat.getDrawable(binding.root.context, R.drawable.rounded_background_pending)
                    binding.btnAction.text = "ACCIÓN"
                    binding.btnAction.setTextColor(ContextCompat.getColor(binding.root.context, R.color.colorPrimary))
                    binding.btnAction.strokeColor = ContextCompat.getColorStateList(binding.root.context, R.color.colorPrimary)
                }
            }

            binding.btnAction.setOnClickListener { onClick(reservation) }
            binding.root.setOnClickListener { onClick(reservation) } // También click en toda la tarjeta
        }
    }

    private class ReservationDiffCallback : DiffUtil.ItemCallback<Reservation>() {
        override fun areItemsTheSame(oldItem: Reservation, newItem: Reservation): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Reservation, newItem: Reservation): Boolean {
            return oldItem == newItem
        }
    }
}