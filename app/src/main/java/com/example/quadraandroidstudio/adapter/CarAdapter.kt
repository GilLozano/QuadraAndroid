package com.example.quadraandroidstudio.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quadraandroidstudio.databinding.ItemCarBinding
import com.example.quadraandroidstudio.model.Car
import com.example.quadraandroidstudio.R

class CarAdapter(private val onReserveClick: (Car) -> Unit) :
    ListAdapter<Car, CarAdapter.CarViewHolder>(CarDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val binding = ItemCarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val car = getItem(position)
        holder.bind(car)
    }

    inner class CarViewHolder(private val binding: ItemCarBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(car: Car) {
            binding.textViewCarName.text = "${car.marca} ${car.modelo}"
            binding.textViewCarDetails.text = "Año: ${car.anio} - Asientos: ${car.asientos}"
            binding.textViewCarPrice.text = "Precio: $${car.precioPorDia}/día"

           /*
            val resourceId = itemView.context.resources.getIdentifier(
                car.imagen ?: "ic_car_placeholder",
                "drawable",
                itemView.context.packageName
            )
            binding.imageViewCar.setImageResource(resourceId)

            // Si 'imagen' es una URL completa (ej. "https://...")
            // librería como Glide o Coil
            */
            Glide.with(itemView.context)
                .load(car.imagen) // La URL o nombre de archivo que viene de la BD
                .placeholder(R.drawable.ic_car_placeholder) // Imagen a mostrar mientras carga
                .error(R.drawable.ic_car_placeholder) // Imagen a mostrar si falla la carga o el link está roto
                .centerCrop() // Ajusta la imagen para llenar el espacio sin deformarse (recorta si es necesario)
                // .fitCenter() // Usa este si prefieres que se vea toda la imagen, aunque queden espacios vacíos
                .into(binding.imageViewCar)

            binding.buttonReservar.setOnClickListener {
                onReserveClick(car)
            }
        }
    }

    private class CarDiffCallback : DiffUtil.ItemCallback<Car>() {
        override fun areItemsTheSame(oldItem: Car, newItem: Car): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Car, newItem: Car): Boolean {
            return oldItem == newItem
        }
    }
}