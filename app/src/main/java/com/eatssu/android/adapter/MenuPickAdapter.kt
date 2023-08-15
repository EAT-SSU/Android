package com.eatssu.android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.databinding.ItemMenuPickBinding


class MenuPickAdapter(itemList: ArrayList<String>?) :
    RecyclerView.Adapter<MenuPickAdapter.ViewHolder>() {

    /* 어댑터에 필요한 변수들 */
    private var binding: ItemMenuPickBinding? = null
    private val wordsItemList: ArrayList<String>?

    inner class ViewHolder(private val binding: ItemMenuPickBinding) :
        RecyclerView.ViewHolder(binding.root) {
        lateinit var checkBox: CheckBox

        fun bind(position: Int) {
//            var checkBox: CheckBox

            checkBox = binding.checkBox

            binding.tvMenuName.text = wordsItemList!![position]

        }

//        init {
//            checkBox = itemView.findViewById(R.id.checkbox)
//        }
    }

    /* 리스너 인터페이스 구현부 */
    interface CheckBoxClickListener {
        fun onClickCheckBox(flag: Int, pos: Int)
    }

    /* 체크박스 리스너 */
    private var mCheckBoxClickListener: CheckBoxClickListener? = null

    /* 어댑터 생성자 */
    init {
        this.wordsItemList = itemList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemMenuPickBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)


//        val wordsItem: String = wordsItemList!![position]
//        binding?.tvMenuName!!.text = wordsItem


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
        return wordsItemList!!.size
    }


    /* 리스너 메소드 */
    fun setOnCheckBoxClickListener(clickCheckBoxListener: CheckBoxClickListener?) {
        mCheckBoxClickListener = clickCheckBoxListener
    }
}