package com.example.educateverse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var handler: Handler

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()//hiding action bar
        setContentView(R.layout.activity_main)

        getWindow().setFlags(FLAG_FULLSCREEN,FLAG_FULLSCREEN)

        handler=Handler()
        handler.postDelayed({
            var i=Intent(this,Login::class.java)
            startActivity(i)
            finish()
        },4400)//delay in seconds
    }
}