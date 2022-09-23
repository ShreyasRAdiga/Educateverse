package com.example.educateverse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View

class AccountCreated : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_created)


    }

    fun next(view: View?) {
        val intent = Intent(this, Login::class.java)
        this.startActivity(intent)
    }
}