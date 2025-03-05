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
import android.app.DatePickerDialog
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

val catToNum : Map<String,Int> = mapOf("🍔 Food" to 0, "🚕 Transport" to 1, "💄 Beauty" to 2,
            "🎁 Gift" to 3, "🏠 Household" to 4, "🎓 Education" to 5)
val categories = arrayOf("🍔 Food", "🚕 Transport", "💄 Beauty", "🎁 Gift", "🏠 Household",
        "🎓 Education")

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
        etDate.setOnClickListener{
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Format the selected date and set it to the EditText
                    val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    etDate.setText(formattedDate)  // Set the selected date in EditText
                    Toast.makeText(this, "Selected Date: $formattedDate",
                                    Toast.LENGTH_SHORT).show()
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        btnSave.setOnClickListener{
            val amt = etAmount.text.toString().toDoubleOrNull()
            val note = etNote.text.toString()
            val cat = catToNum[tvCategoryValue.text.toString()]
            val date = etDate.text.toString()
            if(cat != null && amt != null && date.isNotEmpty()){
                val dateInMillis = convertDateToMillis(date)
                val newTransaction = Transaction(amount = amt, date = dateInMillis, note = note,
                    category = cat)
            }else{
                Toast.makeText(this, "Please add empty fields", Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun convertDateToMillis(date: String): Long {
        val format = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        return (format.parse(date).time)
    }

    private fun showPopGridView(tvCategoryValue: TextView) {

        val inflater = LayoutInflater.from(this)
        val layout = inflater.inflate(R.layout.popup_grid, null)

        Log.d("ActivityAdd", "showPopGridView: before gridview")

        val gridView : GridView = layout.findViewById(R.id.gridItem)

        Log.d("ActivityAdd", "showPopGridView: after categories")

        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,categories)
        gridView.adapter = adapter

        val popupWindow = PopupWindow(layout,WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,true)
        popupWindow.showAtLocation(tvCategoryValue,android.view.Gravity.BOTTOM,0,0)

        gridView.setOnItemClickListener { _, _, position, _ ->
            tvCategoryValue.text = categories[position]
            popupWindow.dismiss()
        }

    }
}
