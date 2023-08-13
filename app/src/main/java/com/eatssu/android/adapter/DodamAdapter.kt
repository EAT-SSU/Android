package com.eatssu.android.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.data.model.response.GetTodayMealResponse
import com.eatssu.android.databinding.ItemDodamBinding
import com.eatssu.android.ui.review.MenuPickActivity
import com.eatssu.android.ui.review.ReviewListActivity



class DodamAdapter(private val dataList: GetTodayMealResponse) :
    RecyclerView.Adapter<DodamAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemDodamBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(position: Int) {
            if (position >= 0 && position < dataList.size) {
                val menuList = dataList[position]
                val nameList = StringBuilder()

                for (menuInfo in menuList.changeMenuInfoList) {
                    nameList.append(menuInfo.name)
                    nameList.append("+")
                }

                if (nameList.isNotEmpty()) {
                    nameList.deleteCharAt(nameList.length - 1) // Remove the last '+'
                }

                val result = nameList.toString()
                binding.tvMenu.text = result

                binding.tvPrice.text = dataList[position].price.toString()
                binding.tvRate.text = dataList[position].mainGrade.toString()
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemDodamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)

        //서버 연결
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ReviewListActivity::class.java)
            intent.putExtra(
                "mealId", dataList[position].mealId
            )
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }

    override fun getItemCount(): Int = dataList.size
}