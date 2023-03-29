package com.eatssu.android

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DodamAdapter(val itemList: ArrayList<Dodam>) :
    RecyclerView.Adapter<DodamAdapter.DodamViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DodamViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dodam, parent, false)
        return DodamViewHolder(view)
    }

    override fun onBindViewHolder(holder: DodamViewHolder, position: Int) {
        holder.item_menu.text = itemList[position].menu
        holder.item_price.text = itemList[position].price
        holder.item_rate.text = itemList[position].rate.toString()
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }


    inner class DodamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item_menu = itemView.findViewById<TextView>(R.id.item_edt_contents)
        val item_price = itemView.findViewById<TextView>(R.id.item_edt_price)
        val item_rate = itemView.findViewById<TextView>(R.id.item_edt_rate)
    }
}