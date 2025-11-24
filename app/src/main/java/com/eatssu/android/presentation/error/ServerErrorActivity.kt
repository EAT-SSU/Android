package com.eatssu.android.presentation.error

import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.eatssu.android.R

class ServerErrorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_server_error)

        val title = intent.getStringExtra(EXTRA_TITLE) ?: getString(R.string.server_error_title)
        val message =
            intent.getStringExtra(EXTRA_MESSAGE) ?: getString(R.string.server_error_message)

        showServerErrorDialog(title, message)
    }

    private fun showServerErrorDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                finishAffinity()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_MESSAGE = "extra_message"
    }
}

