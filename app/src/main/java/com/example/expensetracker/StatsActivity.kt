package com.example.expensetracker

import android.os.Bundle
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity


class StatsActivity : AppCompatActivity(){

    val monthSpinner: Spinner by lazy { findViewById(R.id.monthSpinner) }
    val yearSpinner: Spinner by lazy { findViewById(R.id.yearSpinner) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val tvStatsExpense = findViewById<TextView>(R.id.tvStatsExpense)
        val tvStatsIncome = findViewById<TextView>(R.id.tvStatsIncome)
        val ivTransac = findViewById<ImageView>(R.id.ivTransac)
        val ivStats = findViewById<ImageView>(R.id.ivStats)
        val ivLeftArrow = findViewById<ImageView>(R.id.ivLeftArrow)
        val ivRightArrow = findViewById<ImageView>(R.id.ivRightArrow)

        val myViewModel : MonthYearViewModel by viewModels()
        MainActivity.setupSpinner(monthSpinner,yearSpinner,myViewModel, this)


    }
}