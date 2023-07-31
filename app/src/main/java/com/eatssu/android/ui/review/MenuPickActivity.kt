package com.eatssu.android.ui.review

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.eatssu.android.R
import com.eatssu.android.adapter.MenuPickAdapter
import com.eatssu.android.databinding.ActivityMenuPickBinding
import com.eatssu.android.databinding.ActivityReportBinding
import com.eatssu.android.entity.MenuPickItem


class MenuPickActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuPickBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuPickBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* 리사이클러뷰 어댑터 */
//        val menuPickAdapter = MenuPickActivity(Context, MenuPickItem)
//        binding.rvMenuPicker.setHasFixedSize(true)
//
//        menuPickAdapter.setOnCheckBoxClickListener(object : MenuPickAdapter.CheckBoxClickListener() {
//            fun onClickCheckBox(flag: Int, pos: Int) {
//                /* 체크박스를 눌렀을 때 리스너로 상태(눌렸는지, 위치)를 불러옴 */
//                Log.d("__star__", flag.toString() + "" + pos)
//            }
//        })
//        binding.rvMenuPicker.setAdapter(menuPickAdapter)
    }



}