package com.example.educateverse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class Error : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)
    }

    fun okay(view: View?) {
        val intent = Intent(this, Login::class.java)
        this.startActivity(intent)
    }
}