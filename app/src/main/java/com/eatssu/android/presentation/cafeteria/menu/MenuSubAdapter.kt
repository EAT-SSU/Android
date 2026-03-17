package com.eatssu.android.presentation.cafeteria.menu

import android.content.Intent
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.databinding.ItemMenuBinding
import com.eatssu.android.domain.model.Menu
import com.eatssu.android.presentation.cafeteria.review.ReviewComposeActivity
import com.eatssu.common.analytics.CafeteriaAnalyticsEvent
import com.eatssu.common.analytics.AnalyticsTracker
import com.eatssu.common.enums.MenuType
import com.eatssu.common.enums.Restaurant


class MenuSubAdapter(
    private val dataList: List<Menu>,
    private val restaurant: Restaurant,
    private val analyticsTracker: AnalyticsTracker,
) :
    RecyclerView.Adapter<MenuSubAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var lastClickTimeMs: Long = 0L

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener

                val now = SystemClock.elapsedRealtime()
                if (now - lastClickTimeMs < 600) return@setOnClickListener
                lastClickTimeMs = now

                // Prevent duplicate clicks at the View level
                if (!binding.root.isEnabled) return@setOnClickListener
                binding.root.isEnabled = false

                val item = dataList[position]
                val intent = Intent(binding.root.context, ReviewComposeActivity::class.java)

                when (restaurant.menuType) {
                    MenuType.FIXED -> {
                        Log.d("SubMenuAdapter", "고정메뉴${item.name}")
                        intent.putExtra("itemId", item.id)
                        intent.putExtra("itemName", item.name)
                        intent.putExtra("menuType", MenuType.FIXED.toString())
                    }

                    MenuType.VARIABLE -> {
                        Log.d("SubMenuAdapter", "변동메뉴${item.name}")
                        intent.putExtra("itemId", item.id)
                        intent.putExtra("itemName", item.name)
                        intent.putExtra("menuType", MenuType.VARIABLE.toString())
                    }
                }
                ContextCompat.startActivity(binding.root.context, intent, null)
                analyticsTracker.track(CafeteriaAnalyticsEvent.MenuClicked(restaurant))
                // Re-enable after short delay
                binding.root.postDelayed({ binding.root.isEnabled = true }, 800)
            }
        }

        fun bind(position: Int) {
            binding.tvMenu.text = dataList[position].name
            binding.tvPrice.text = dataList[position].price.toString()
            binding.tvRate.text =
                when (dataList[position].rate.toString()) {
                    "0.0" -> "-"
                    "NaN" -> "-"
                    "null" -> "-"
                    else -> String.format("%.1f", dataList[position].rate)
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
    }

    override fun getItemCount(): Int = dataList.size
}
