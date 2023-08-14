package com.eatssu.android.adapter

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.databinding.ItemMenuPickBinding
import com.eatssu.android.data.entity.MenuPickItem


class MenuPickAdapter (private val context: Context, wordsItemList: List<MenuPickItem>) :
    RecyclerView.Adapter<MenuPickAdapter.ViewHolder>() {
    /* 어댑터에 필요한 변수들 */
    private var binding: ItemMenuPickBinding? = null
    private val wordsItemList: List<MenuPickItem>

    /* 리스너 인터페이스 구현부 */
    interface CheckBoxClickListener {
        fun onClickCheckBox(flag: Int, pos: Int)
    }

    /* 체크박스 리스너 */
    private var mCheckBoxClickListener: CheckBoxClickListener? = null

    /* 어댑터 생성자 */
    init {
        this.wordsItemList = wordsItemList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemMenuPickBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding!!.getRoot())
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val wordsItem: MenuPickItem = wordsItemList[position]
        binding?.tvMenuName!!.text = wordsItem.menuName


        /* 체크박스 리스너 */holder.checkBox.setOnClickListener { v: View? ->
            if (holder.checkBox.isChecked) {
                // 체크가 되어 있음
                mCheckBoxClickListener!!.onClickCheckBox(1, position)
            } else {
                // 체크가 되어있지 않음
                mCheckBoxClickListener!!.onClickCheckBox(0, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return wordsItemList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox

        init {
            checkBox = itemView.findViewById(R.id.checkbox)
        }
    }

    /* 리스너 메소드 */
    fun setOnCheckBoxClickListener(clickCheckBoxListener: CheckBoxClickListener?) {
        mCheckBoxClickListener = clickCheckBoxListener
    }
}