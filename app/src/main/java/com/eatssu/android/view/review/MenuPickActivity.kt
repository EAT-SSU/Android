package com.eatssu.android.view.review

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.adapter.MenuPickAdapter
import com.eatssu.android.databinding.ActivityMenuPickBinding


class MenuPickActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuPickBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuPickBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val itemName = intent.getStringArrayListExtra("itemName")
        Log.d("post","받은"+itemName.toString())

        /* 리사이클러뷰 어댑터 */
        val menuPickAdapter = MenuPickAdapter(itemName)
        val recyclerView = binding.rvMenuPicker
        recyclerView.adapter = menuPickAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        menuPickAdapter.setOnCheckBoxClickListener(object : MenuPickAdapter.CheckBoxClickListener {
            override fun onClickCheckBox(flag: Int, pos: Int) {
                /* 체크박스를 눌렀을 때 리스너로 상태(눌렸는지, 위치)를 불러옴 */
                Log.d("__star__", flag.toString() + "" + pos)
            }
        })
    }



}