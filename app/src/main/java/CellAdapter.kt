package com.example.poctomatapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CellAdapter(
    private var cells: MutableList<CellEntity>,
    private val onCellClick: (CellEntity) -> Unit
) : RecyclerView.Adapter<CellAdapter.CellViewHolder>() {

    class CellViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById(R.id.cellNumber)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CellViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cell, parent, false)
        return CellViewHolder(view)
    }

    override fun onBindViewHolder(holder: CellViewHolder, position: Int) {
        val cell = cells[position]

        holder.text.text = "${cell.number}\n${cell.size}"

        if (!cell.isOccupied) {
            holder.text.setBackgroundColor(Color.GREEN)
        } else if (isExpired(cell.expiryDate)) {
            holder.text.setBackgroundColor(Color.rgb(255, 165, 0)) // оранжевый
        } else {
            holder.text.setBackgroundColor(Color.RED)
        }

        holder.itemView.setOnClickListener {
            onCellClick(cell)
        }
    }

    override fun getItemCount(): Int = cells.size

    fun updateData(newCells: MutableList<CellEntity>) {
        cells = newCells
        notifyDataSetChanged()
    }

    private fun isExpired(expiryDate: String?): Boolean {
        if (expiryDate.isNullOrEmpty()) return false

        return try {
            val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val expiry = format.parse(expiryDate)
            val today = format.parse(format.format(Date()))
            expiry != null && today != null && today.after(expiry)
        } catch (e: Exception) {
            false
        }
    }
}