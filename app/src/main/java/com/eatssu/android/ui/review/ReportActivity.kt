package com.eatssu.android.ui.review

import android.os.Bundle
import android.view.View
import com.eatssu.android.databinding.ActivityReportBinding
import com.eatssu.android.ui.BaseActivity
import kotlin.properties.Delegates

abstract class ReportActivity : BaseActivity() {
    private lateinit var binding: ActivityReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)

        setActionBarTitle("신고하기")

        setContentView(binding.root)

        val radioGroup = binding.radioGp;
        var radioNum = -1;
        radioGroup.setOnClickListener {
            if (binding.radioBt1.isChecked)
                radioNum = 1;
            else if (binding.radioBt2.isChecked)
                radioNum = 2;
            else if (binding.radioBt3.isChecked)
                radioNum = 3;
            else if (binding.radioBt4.isChecked)
                radioNum = 4;
            else if (binding.radioBt5.isChecked)
                radioNum = 5;
            else if (binding.radioBt6.isChecked)
                radioNum = 6;
        }
    }

    abstract fun setActionBarTitle(s: String)
}