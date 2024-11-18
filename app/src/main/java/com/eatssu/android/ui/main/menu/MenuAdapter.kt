package com.eatssu.android.ui.main.menu

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.R
import com.eatssu.android.domain.model.Section
import com.eatssu.android.databinding.ItemCafeteriaSectionBinding
import com.eatssu.android.ui.info.InfoBottomSheetFragment

class MenuAdapter(
    private val fragmentManager: FragmentManager,
    private val totalMenuList: ArrayList<Section>

) : RecyclerView.Adapter<MenuAdapter.MyViewHolder>() {

    class MyViewHolder(
        private val binding: ItemCafeteriaSectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            fragmentManager: FragmentManager,
            sectionModel: Section
        ) {

            binding.llCafeteriaInfo.setOnClickListener {

                val modalBottomSheet =
                    InfoBottomSheetFragment.newInstance(sectionModel.cafeteria.name)
                modalBottomSheet.setStyle(
                    DialogFragment.STYLE_NORMAL,
                    R.style.RoundCornerBottomSheetDialogTheme
                )
                modalBottomSheet.show(fragmentManager, "Open Bottom Sheet")
                Log.d("MenuAdapter", "bind: ${sectionModel.cafeteria}")
            }

            binding.tvCafeteria.text = sectionModel.cafeteria.displayName
            binding.tvCafeteriaLocation.text = sectionModel.cafeteriaLocation

            binding.rvMenu.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(binding.root.context)
                adapter = sectionModel.menuList?.let {
                    MenuSubAdapter(it, sectionModel.cafeteria.menuType)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemCafeteriaSectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        totalMenuList[position].let { sectionModel ->
            holder.bind(fragmentManager, sectionModel)
        }
    }

    override fun getItemCount(): Int = totalMenuList.size

}