package com.eatssu.android.presentation.cafeteria.menu

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.R
import com.eatssu.android.databinding.ItemCafeteriaSectionBinding
import com.eatssu.android.domain.model.Section
import com.eatssu.android.presentation.cafeteria.info.InfoBottomSheetFragment
import com.eatssu.common.analytics.AnalyticsTracker

class MenuAdapter(
    private val fragmentManager: FragmentManager,
    private val sectionList: List<Section>,
    private val analyticsTracker: AnalyticsTracker,
) : RecyclerView.Adapter<MenuAdapter.MyViewHolder>() {

    class MyViewHolder(
        private val binding: ItemCafeteriaSectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            fragmentManager: FragmentManager,
            sectionModel: Section,
            analyticsTracker: AnalyticsTracker,
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

            binding.tvCafeteria.text = binding.root.context.getString(sectionModel.cafeteria.displayNameResId)
            binding.tvCafeteriaLocation.text = sectionModel.cafeteriaLocation

            binding.rvMenu.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(binding.root.context)
                adapter = sectionModel.menuList?.let {
                    MenuSubAdapter(it, sectionModel.cafeteria, analyticsTracker)
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
        sectionList[position].let { sectionModel ->
            holder.bind(fragmentManager, sectionModel, analyticsTracker)
        }
    }

    override fun getItemCount(): Int = sectionList.size

}
