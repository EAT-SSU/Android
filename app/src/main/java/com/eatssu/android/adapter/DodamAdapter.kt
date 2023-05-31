package com.eatssu.android.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.data.model.response.GetMenuInfoListResponse
import com.eatssu.android.databinding.ItemDodamBinding
import com.eatssu.android.ui.review.ReviewListActivity


class DodamAdapter(private val dataList: List<GetMenuInfoListResponse>) :
    RecyclerView.Adapter<DodamAdapter.ViewHolder>() {
    val idList: MutableList<Int> = mutableListOf()

    inner class ViewHolder(private val binding: ItemDodamBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(position: Int) {
            if (position >= 0 && position < dataList.size) {
                val menuList = dataList[position]
                val nameList = StringBuilder()

                for (menuInfo in menuList.menuInfoList) {
                    nameList.append(menuInfo.name)
                    nameList.append("+")
                    idList.add(menuInfo.menuId)

                }

//                for (menuInfo in menuList.menuInfoList) {
//                }

                if (nameList.isNotEmpty()) {
                    nameList.deleteCharAt(nameList.length - 1) // Remove the last '+'
                }

                val result = nameList.toString()
                binding.tvMenu.text = result

                binding.tvPrice.text = 6000.toString()

                var totalGrade = 0.0

                for (menuInfo in menuList.menuInfoList) {
                    val grade = menuInfo.grade ?: 0.0
                    totalGrade += grade
                }

                val averageGrade = if (menuList.menuInfoList.isNotEmpty()) {
                    totalGrade / menuList.menuInfoList.size
                } else {
                    0.0
                }

                binding.tvRate.text = String.format("%.1f", averageGrade)
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
//            intent.putExtra(
//                "menuId", dataList[position].menuId
//            )
            intent.putIntegerArrayListExtra("menuIdList", ArrayList(idList))
            Log.d("post","메롱"+idList.toString())
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }

    override fun getItemCount(): Int = dataList.size
}