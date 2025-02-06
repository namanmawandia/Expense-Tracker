package com.example.expensetracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val btnFab: FloatingActionButton = findViewById(R.id.btnFAB)
        val tvDaily = findViewById<TextView>(R.id.tvDaily)
        val tvCalender = findViewById<TextView>(R.id.tvCalender)
        val ivTransac = findViewById<ImageView>(R.id.ivTransac)
        val ivStats = findViewById<ImageView>(R.id.ivStats)



        if(savedInstanceState==null){
            replaeFragment(DailyFragment())
        }

        btnFab.setOnClickListener{
            setAddButton(this)
        }

    }

    private fun replaeFragment(dailyFragment: Fragment) {

        val fragment = DailyFragment()
        val trasaction = supportFragmentManager.beginTransaction()
        trasaction.replace(R.id.frgOuter,fragment)
        trasaction.commit()
    }

    private fun setAddButton(context: Context) {

        val intent = Intent(context,ActivityAdd::class.java)
        Log.d("Main Activity", "setAddButton: Intent to another Add Activty")
        startActivity(intent)

    }
}
