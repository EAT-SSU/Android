package com.eatssu.android.ui.main.menu

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.data.model.response.GetTodayMealResponseDto
import com.eatssu.android.databinding.ItemMenuBinding
import com.eatssu.android.ui.review.list.ReviewListActivity

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
            intent.putExtra("mealId", dataList[position].mealId)
            intent.putExtra(
                "menuType", MenuType.CHANGE.toString()
            )

            val menuIds = mutableListOf<Long>()
            val menuNames = mutableListOf<String>()

            for (item in dataList) {
                for (changeMenuInfo in item.changeMenuInfoList) {
                    menuIds.add(changeMenuInfo.menuId)
                    menuNames.add(changeMenuInfo.name)
                }
            }

            val menuIdArray = menuIds.toLongArray()
            val menuNameArray = ArrayList(menuNames.toList())

            intent.putExtra("menuIdArray", menuIdArray)
            intent.putStringArrayListExtra("menuNameArray", menuNameArray)

            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }

    override fun getItemCount(): Int = dataList.size
}