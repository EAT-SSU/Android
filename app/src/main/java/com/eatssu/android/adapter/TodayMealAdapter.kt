package com.eatssu.android.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.data.model.response.GetTodayMealResponseDto
import com.eatssu.android.databinding.ItemMenuBinding
import com.eatssu.android.view.review.ReviewListActivity
import com.eatssu.android.viewmodel.MenuIdViewModel

class TodayMealAdapter(private val dataList: GetTodayMealResponseDto) :
    RecyclerView.Adapter<TodayMealAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemMenuBinding) :
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
                binding.tvRate.text = String.format("%.1f", dataList[position].mainGrade)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ReviewListActivity::class.java)
            intent.putExtra(
                "itemId", dataList[position].mealId
            )
            intent.putExtra(
                "menuType", MenuType.CHANGE.toString()
            )

            Log.d("adaptermenu", "$dataList[position].mealId")
            val menuViewModel = ViewModelProvider(holder.itemView.context as ViewModelStoreOwner)[MenuIdViewModel::class.java]
            menuViewModel.setData(dataList[position].mealId.toString())

            ContextCompat.startActivity(holder.itemView.context, intent, null)

        }
    }

    override fun getItemCount(): Int = dataList.size
}