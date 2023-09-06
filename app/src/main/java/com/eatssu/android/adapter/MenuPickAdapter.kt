package com.eatssu.android.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.databinding.ItemMenuPickBinding
import com.eatssu.android.view.review.WriteReviewActivity


class MenuPickAdapter(menuNameArray: ArrayList<String>?, menuIdArray: LongArray?) :
    RecyclerView.Adapter<MenuPickAdapter.ViewHolder>() {

    /* 어댑터에 필요한 변수들 */
    private var binding: ItemMenuPickBinding? = null
    private val wordsItemList: ArrayList<String>?
    private val wordsIdList: ArrayList<Long>?


    private val checkedItems: ArrayList<Pair<String, Long>> = ArrayList()


    inner class ViewHolder(private val binding: ItemMenuPickBinding) :
        RecyclerView.ViewHolder(binding.root) {
        lateinit var checkBox: CheckBox

        fun bind(position: Int) {
//            var checkBox: CheckBox

            checkBox = binding.checkBox

            binding.tvMenuName.text = wordsItemList!![position]

        }
    }

    /* 리스너 인터페이스 구현부 */
    interface CheckBoxClickListener {
        fun onClickCheckBox(flag: Int, pos: Int)
    }

    /* 체크박스 리스너 */
    private var mCheckBoxClickListener: CheckBoxClickListener? = null

    /* 어댑터 생성자 */
    init {
        this.wordsItemList = menuNameArray
        this.wordsIdList = menuIdArray?.toList()?.let { ArrayList(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemMenuPickBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)

        /* 체크박스 리스너 */holder.checkBox.setOnClickListener { v: View? ->
            if (holder.checkBox.isChecked) {
                // 체크가 되어 있음
//                mCheckBoxClickListener!!.onClickCheckBox(1, position)
                Log.d("post", wordsItemList?.get(position) + wordsIdList!![position])

                val menuName = wordsItemList!![position]
                val menuId = wordsIdList[position]
                checkedItems.add(Pair(menuName, menuId))

            } else {
                // 체크가 되어있지 않음
//                mCheckBoxClickListener!!.onClickCheckBox(0, position)
                val menuName = wordsItemList!![position]
                val menuId = wordsIdList!![position]
                checkedItems.remove(Pair(menuName, menuId))

            }
        }

        Log.d("post", checkedItems.toString())

    }

    override fun getItemCount(): Int {
        return wordsItemList!!.size
    }


//    /* 리스너 메소드 */
//    fun setOnCheckBoxClickListener(clickCheckBoxListener: CheckBoxClickListener?) {
//        mCheckBoxClickListener = clickCheckBoxListener
//    }

    fun sendItem(): ArrayList<Pair<String, Long>> {
//        val context = binding!!.root.context
//        val intent = Intent(context, WriteReviewActivity::class.java)
//        intent.putExtra("checkedItems", checkedItems)
//        Log.d("post", checkedItems.toString())
//
//        context.startActivity(intent)
        Log.d("post", "넘겨$checkedItems")

        return checkedItems
    }
//    fun openReviewActivity() {
//        val intent = Intent(context, WriteReviewActivity::class.java)
//
//        // 체크된 항목 리스트를 인텐트에 전달
//        intent.putExtra("checkedItems", checkedItems)
//        Log.d("post", checkedItems.toString())
//
//        context.startActivity(intent)
//    }

}