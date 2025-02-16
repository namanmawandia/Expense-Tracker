package com.example.expensetracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.activity.ComponentActivity

class ActivityAdd : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add)

        val toolbar : Toolbar = findViewById(R.id.toolbar)
        val etDate : EditText = findViewById(R.id.etDate)
        val etAmount : EditText = findViewById(R.id.etAmount)
        val etNote : EditText = findViewById(R.id.etNote)
        val tvCategoryValue : TextView = findViewById(R.id.tvCategoryValue)
        val btnSave : Button = findViewById(R.id.btnSave)

        Log.d("addactivty", "onCreate: ")
        tvCategoryValue.setOnClickListener{
            showPopGridView(tvCategoryValue)
        }

        btnSave.setOnClickListener{

        }

    }

    private fun showPopGridView(tvCategoryValue: TextView) {

        val inflater = LayoutInflater.from(this)
        val layout = inflater.inflate(R.layout.popup_grid, null)

        Log.d("ActivityAdd", "showPopGridView: before gridview")

        val gridView : GridView = layout.findViewById(R.id.gridItem)
        val categories = arrayOf("🍔 Food", "🚕 Transport", "💄 Beauty", "🎁 Gift", "🏠 Household", "🎓 Education")

        Log.d("ActivityAdd", "showPopGridView: after categories")

        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,categories)
        gridView.adapter = adapter

        val popupWindow = PopupWindow(layout,WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT,true)
        popupWindow.showAtLocation(tvCategoryValue,android.view.Gravity.BOTTOM,0,0)

        gridView.setOnItemClickListener { _, _, position, _ ->
            tvCategoryValue.text = categories[position]
            popupWindow.dismiss()
        }

    }
}
