package com.example.expensetracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import kotlin.Pair
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar


class MainActivity : AppCompatActivity() {

    val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct",
        "Nov", "Dec")
    val years = (2010..2040).toList()
    var globalMonth: Int = 0
    var globalYear : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val btnFab: FloatingActionButton = findViewById(R.id.btnFAB)
        val tvDaily = findViewById<TextView>(R.id.tvDaily)
        val tvCalender = findViewById<TextView>(R.id.tvCalender)
        val ivTransac = findViewById<ImageView>(R.id.ivTransac)
        val ivStats = findViewById<ImageView>(R.id.ivStats)
        val monthSpinner = findViewById<Spinner>(R.id.monthSpinner)
        val yearSpinner = findViewById<Spinner>(R.id.yearSpinner)
        val ivLeftArrow = findViewById<ImageView>(R.id.ivLeftArrow)
        val ivRightArrow = findViewById<ImageView>(R.id.ivRightArrow)

        // setting up spinner
        setupSpinner(monthSpinner,yearSpinner)

        if(savedInstanceState==null){
            replaceFragment(DailyFragment())
            setTargetActivity(MainActivity::class.java)
            highlightSelectedTab(R.id.ivTransac)
            highlightSelectedFrag(R.id.tvDaily)
        }
        tvDaily.setOnClickListener{
            replaceFragment(DailyFragment())
            highlightSelectedFrag(R.id.tvDaily)
        }
        tvCalender.setOnClickListener{
            replaceFragment(CalenderFragment())
            highlightSelectedFrag(R.id.tvCalender)
        }
        ivTransac.setOnClickListener{
            setTargetActivity(MainActivity::class.java)
            highlightSelectedTab(R.id.ivTransac)
        }
        ivStats.setOnClickListener{
            setTargetActivity(StatsActivity::class.java)
            highlightSelectedTab(R.id.ivStats)
        }
        btnFab.setOnClickListener{
            val intent = Intent(this,ActivityAdd::class.java)
            Log.d("Main Activity", "setAddButton: Intent to Add Activity")
            startActivity(intent)
        }
//        monthSpinner.setOnClickListener{
//            TODO("left")
//        }
//        yearSpinner.setOnClickListener{
//            TODO("Left")
//        }
        ivLeftArrow.setOnClickListener{
            if(globalMonth==0) {
                globalMonth = 11
                globalYear--
            } else globalMonth--
            monthSpinner.setSelection(globalMonth)
            yearSpinner.setSelection(globalYear)
        }
        ivRightArrow.setOnClickListener{
            if(globalMonth==11) {
                globalMonth = 0
                globalYear++
            } else globalMonth++
            monthSpinner.setSelection(globalMonth)
            yearSpinner.setSelection(globalYear)
        }

    }

    private fun setupSpinner(monthSpinner: Spinner, yearSpinner: Spinner) {
        val monthAdapter = ArrayAdapter(this, R.layout.spinner_item_bar_layout, months)
        val yearAdapter = ArrayAdapter(this, R.layout.spinner_item_bar_layout, years)
        val cal = Calendar.getInstance()
        globalMonth = cal.get(Calendar.MONTH)
        globalYear  = cal.get(Calendar.YEAR) -2010
        monthAdapter.setDropDownViewResource(R.layout.spinner_item_bar_layout)
        yearAdapter.setDropDownViewResource(R.layout.spinner_item_bar_layout)
        monthSpinner.adapter = monthAdapter
        yearSpinner.adapter = yearAdapter
        monthSpinner.setSelection(globalMonth)
        yearSpinner.setSelection(globalYear)
    }

    private fun setTargetActivity(targetActivity: Class<*>) {
        if(this::class.java != targetActivity) {
            val intent = Intent(this, StatsActivity::class.java)
            Log.d("Main Activity", "setStatsActivity: Intent to Stats Activity")
            startActivity(intent)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frgOuter,fragment)
        transaction.commit()
    }

    fun highlightSelectedTab(selectedId: Int) {
        val tabs = listOf(
            Pair(R.id.ivTransac , R.id.tvTrans),
            Pair(R.id.ivStats , R.id.tvStats),
        )

        for ((iconId, textId) in tabs) {
            val icon = findViewById<ImageView>(iconId)
            val text = findViewById<TextView>(textId)

            val isSelected = iconId == selectedId
            val color = ContextCompat.getColor(this,
                if (isSelected) R.color.negativeTransac else R.color.primaryLight2)
            icon.setColorFilter(color)
            text.setTextColor(color)
        }
    }

    fun highlightSelectedFrag(selectedId: Int){
        val frags = listOf(R.id.tvDaily, R.id.tvCalender)

        for(textId in frags){
            val text = findViewById<TextView>(textId)
            val isSelected = textId == selectedId
            val color = ContextCompat.getColor(this,
                if(isSelected) R.color.negativeTransac else R.color.primaryLight2)
            text.setTextColor(color)
        }
    }

}
