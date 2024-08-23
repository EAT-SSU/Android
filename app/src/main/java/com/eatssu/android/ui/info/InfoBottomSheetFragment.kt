package com.eatssu.android.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eatssu.android.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class InfoBottomSheetFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_bottomsheet_info, container, false)
        return view
    }
}
