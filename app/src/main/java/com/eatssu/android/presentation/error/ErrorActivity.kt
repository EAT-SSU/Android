package com.eatssu.android.presentation.error

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eatssu.android.databinding.ActivityIntroBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ErrorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showDialog()
    }

    private fun showDialog() {
        val message = intent.getStringExtra("message") ?: "알 수 없는 문제가 발생했습니다. 잠시 후 다시 시도해 주세요."

        AlertDialog.Builder(this)
            .setTitle("알림")
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("확인") { _, _ -> finish() }
            .setOnDismissListener { finish() }
            .show()
    }
}