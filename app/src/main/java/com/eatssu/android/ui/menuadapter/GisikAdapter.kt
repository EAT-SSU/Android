package com.eatssu.android.ui.menuadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.R
import com.eatssu.android.data.model.Gisik

class GisikAdapter (val itemList: ArrayList<Gisik>) :
    RecyclerView.Adapter<GisikAdapter.GisikViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GisikViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gisik, parent, false)
        return GisikViewHolder(view)
    }

    override fun onBindViewHolder(holder: GisikViewHolder, position: Int) {
        holder.item_menu.text = itemList[position].menu
        holder.item_price.text = itemList[position].price
        holder.item_rate.text = itemList[position].rate.toString()
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }


    inner class GisikViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item_menu = itemView.findViewById<TextView>(R.id.item_edt_contents)
        val item_price = itemView.findViewById<TextView>(R.id.item_edt_price)
        val item_rate = itemView.findViewById<TextView>(R.id.item_edt_rate)
    }
}