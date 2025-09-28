package com.example.quadraandroidstudio.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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
            binding.textViewCarName.text = car.name
            binding.textViewCarDetails.text = "Marca: ${car.brand}"
            binding.textViewCarPrice.text = "Precio: $${car.pricePerDay}/día"

            // Cargar imagen desde drawables (asumiendo que los nombres coinciden)
            val resourceId = itemView.context.resources.getIdentifier(
                car.imageUrl, "drawable", itemView.context.packageName
            )
            if (resourceId != 0) {
                binding.imageViewCar.setImageResource(resourceId)
            } else {
                // Si la imagen no se encuentra, puedes poner un placeholder
                binding.imageViewCar.setImageResource(R.drawable.ic_car_placeholder) // Asegúrate de tener este drawable
            }

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