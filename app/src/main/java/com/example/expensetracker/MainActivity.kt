package com.example.expensetracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val btnFab: FloatingActionButton = findViewById(R.id.btnFAB)


        btnFab.setOnClickListener{
            setAddButton(this)
        }

    }

    private fun setAddButton(context: Context) {

        val intent = Intent(context,ActivityAdd::class.java)
        Log.d("Main Activity", "setAddButton: Intent to another Add Activty")
        startActivity(intent)

    }
}
