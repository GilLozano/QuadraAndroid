package com.example.quadraandroidstudio.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quadraandroidstudio.R
import com.example.quadraandroidstudio.databinding.ItemReservationBinding // Asegúrate de que el nombre coincida con tu XML
import com.example.quadraandroidstudio.model.Reservation
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class ReservationAdapter(private val onItemClick: (Reservation) -> Unit) :
    ListAdapter<Reservation, ReservationAdapter.ReservationViewHolder>(ReservationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val binding = ItemReservationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReservationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ReservationViewHolder(private val binding: ItemReservationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(reservation: Reservation) {
            val vehiculo = reservation.vehiculo

            // 1. Nombre del Auto (Modelo)
            binding.tvCarName.text = vehiculo?.modelo ?: "Vehículo Desconocido"

            // 2. Marca y Año
            binding.tvCarBrandYear.text = if (vehiculo != null) {
                "Marca: ${vehiculo.marca} - Año: ${vehiculo.anio}"
            } else {
                "Detalles no disponibles"
            }

            // 3. Precio y Fechas
            // Combino el precio total con el rango de fechas formateado
            val formattedDates = formatReservationDates(reservation.fechaInicio, reservation.fechaFin)
            val totalAmount = "$%.2f".format(reservation.alquiler.monto)
            binding.tvCarPrice.text = "Total: $totalAmount • $formattedDates"

            // 4. Estado (Color y Texto)
            val (statusText, statusBackground) = getStatusDetails(reservation.estado)
            binding.tvStatusTag.text = statusText
            binding.tvStatusTag.setBackgroundResource(statusBackground)


            // 5. Cargar Imagen con Glide
            if (vehiculo?.imagen != null) {
                Glide.with(itemView.context)
                    .load(vehiculo.imagen)
                    .placeholder(R.drawable.ic_car_placeholder)
                    .error(R.drawable.ic_car_placeholder)
                    .centerCrop()
                    .into(binding.ivCarImage)
            } else {
                binding.ivCarImage.setImageResource(R.drawable.ic_car_placeholder)
            }

            // Configurar el botón de acción (por ahora solo muestra un Toast)
            binding.btnAction.text = "VER DETALLES"
            binding.btnAction.setOnClickListener {
                onItemClick(reservation)
            }
        }

        // Helper para formatear fechas (Ej: "25 Nov - 28 Nov")
        private fun formatReservationDates(start: String, end: String): String {
            try {
                // 1. Formato de ENTRADA (cómo viene del servidor: UTC)
                // Esto está bien, le decimos que lea la fecha como UTC.
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")

                // 2. Formato de SALIDA (cómo lo ve el usuario: día y mes)
                val outputFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

                // --- LA SOLUCIÓN ESTÁ EN ESTA LÍNEA NUEVA ---
                // Le decimos: "Cuando muestres la fecha, úsala en UTC también".
                // Esto evita que Android le reste las horas de tu país y se regrese al día anterior.
                outputFormat.timeZone = TimeZone.getTimeZone("UTC")
                // --------------------------------------------

                val startDate = inputFormat.parse(start)
                val endDate = inputFormat.parse(end)

                return "${outputFormat.format(startDate!!)} - ${outputFormat.format(endDate!!)}"
            } catch (_: Exception) {
                return "" // Si falla, no mostramos fecha
            }
        }

        // Helper para obtener el texto y color del estado
        private fun getStatusDetails(estado: String): Pair<String, Int> {
            // Mapea los estados de tu backend a colores y textos para la UI
            // Asegúrate de que los nombres de los estados coincidan EXACTAMENTE con los de tu API
            return when (estado) {
                "Vehiculo En Proceso de Entrega" -> Pair("PENDIENTE", R.drawable.rounded_background_pending)
                "Activa" -> Pair("EN CURSO", R.drawable.rounded_background_active) // Crea este drawable (verde)
                "Finalizada" -> Pair("COMPLETADA", R.drawable.rounded_background_completed) // Crea este (azul/gris)
                "Cancelada" -> Pair("CANCELADA", R.drawable.rounded_background_cancelled) // Crea este (rojo)
                else -> Pair(estado.uppercase(), R.drawable.rounded_background_pending) // Fallback
            }
        }
    }

    class ReservationDiffCallback : DiffUtil.ItemCallback<Reservation>() {
        override fun areItemsTheSame(oldItem: Reservation, newItem: Reservation) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Reservation, newItem: Reservation) = oldItem == newItem
    }
}