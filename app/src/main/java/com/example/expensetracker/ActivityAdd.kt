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
import android.app.DatePickerDialog
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import java.text.SimpleDateFormat
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.ParseException
import java.util.*
import kotlin.time.Duration.Companion.days


val catExpenseToNum : Map<String,Int> = mapOf("🍔 Food" to 0, "🚕 Transport" to 1, "💄 Beauty" to 2,
            "🎁 Gift" to 3, "🏠 Household" to 4, "🎓 Education" to 5)
val categoriesExpense = arrayOf("🍔 Food", "🚕 Transport", "💄 Beauty", "🎁 Gift", "🏠 Household",
        "🎓 Education")
val catIncomeToNum : Map<String,Int> = mapOf("\uD83D\uDCB5 Wages" to 0, "\uD83E\uDDFE Salary" to 1,
    "\uD83D\uDCC8 Commissions" to 2,
    "\uD83D\uDCB0 Tips" to 3, "\uD83C\uDF81 Bonus" to 4, "\uD83D\uDCBC Freelancing" to 5)
val categoriesIncome = arrayOf("\uD83D\uDCB5 Wages","\uD83E\uDDFE Salary","\uD83D\uDCC8 Commissions",
    "\uD83D\uDCB0 Tips", "\uD83C\uDF81 Bonus", "\uD83D\uDCBC Freelancing")

class ActivityAdd : AppCompatActivity() {

    private lateinit var viewModel: TransactionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add)

        val toolbar : Toolbar = findViewById(R.id.toolbar)
        val etDate : EditText = findViewById(R.id.etDate)
        val etAmount : EditText = findViewById(R.id.etAmount)
        val etNote : EditText = findViewById(R.id.etNote)
        val tvCategoryValue : TextView = findViewById(R.id.tvCategoryValue)
        val btnSave : Button = findViewById(R.id.btnSave)
        val spinnerType : Spinner = findViewById(R.id.typeSpinner)
        val btnDel : Button = findViewById(R.id.btnDelete)

        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
        val transactionViewModel : TransactionViewModel by viewModels()
        var type = 0 // for expense or income

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val delPresent = intent.getBooleanExtra("main", false)
        val idTransaction = intent.getIntExtra("dayTransacAdapter",-1)

        Toast.makeText(this, ""+delPresent + " " + idTransaction, Toast.LENGTH_SHORT).show()

        if(delPresent)
            btnDel.visibility = View.GONE
        else{
            val cal = Calendar.getInstance()
            lifecycleScope.launch {
                val oldTra = viewModel.getTransactionById(idTransaction)
                if (oldTra != null) { cal.timeInMillis = oldTra.date }
                etAmount.setText(oldTra?.amount.toString())
                etNote.setText(oldTra?.note)
                tvCategoryValue.text =
                    if(oldTra?.type==0) categoriesExpense[oldTra.category]
                    else categoriesIncome[oldTra?.category!!]
            }
            val setDay = cal.get(Calendar.DAY_OF_MONTH)
            val setMonth = cal.get(Calendar.MONTH)
            val setYear = cal.get(Calendar.YEAR)
            val formattedDate = String.format("%02d/%02d/%d", setDay, setMonth + 1, setYear)
            etDate.setText(formattedDate)

        }

        setUpSpinner(spinnerType)

        Log.d("addactivty", "onCreate: ")

        val initDate = intent.getIntExtra("Date",1)
        val initMonth = intent.getIntExtra("Month",0)
        val initYear = intent.getIntExtra("Year",2010)

        etDate.setText(String.format("%02d/%02d/%d", initDate, initMonth, initYear))

        tvCategoryValue.setOnClickListener{
            btnDel.visibility = View.GONE
            showPopGridView(tvCategoryValue,type,btnSave)
        }

        btnDel.setOnClickListener{
            if(idTransaction==-1)
                Toast.makeText(this, "No record present for following", Toast.LENGTH_SHORT).show()
            else
                viewModel.delWithID(idTransaction)
            finish()
        }

        etDate.setOnClickListener{
            btnDel.visibility = View.GONE
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this, R.style.Theme_DatePicker,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
                    etDate.setText(formattedDate)  // Set the selected date in EditText
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        etAmount.setOnClickListener{ btnDel.visibility = View.GONE }
        etNote.setOnClickListener{ btnDel.visibility = View.GONE }

        btnSave.setOnClickListener{
            val amt = etAmount.text.toString().toDoubleOrNull()
            val note = etNote.text.toString()
            val cat = if(type==0) catExpenseToNum[tvCategoryValue.text.toString()]
                else catIncomeToNum[tvCategoryValue.text.toString()]
            val date = etDate.text.toString()

            if(cat != null && amt != null && date.isNotEmpty()){
                val dateInMillis = try {
                    convertDateToMillis(date)
                } catch (e: ParseException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Invalid date format!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if(idTransaction==-1) {
                    val newTransaction = Transaction(amount = amt, date = dateInMillis,
                        note = note, category = cat, type = type)
                    transactionViewModel.insertTransaction(newTransaction)
                    Toast.makeText(this, "Transaction saved!", Toast.LENGTH_SHORT).show()
                }
                else {
                    lifecycleScope.launch {
                        val oldTransac = viewModel.getTransactionById(idTransaction)
                        if (amt == oldTransac?.amount && note == oldTransac.note && type == oldTransac.type
                            && cat == oldTransac.category && dateInMillis == oldTransac.date
                        )
                            Toast.makeText(this@ActivityAdd, "Transaction not changed", Toast.LENGTH_SHORT)
                                .show()
                        else {
                            val updateT = Transaction(
                                amount = amt, date = dateInMillis,
                                note = note, category = cat, type = type, id = idTransaction
                            )
                            transactionViewModel.updateTransaction(updateT)
                            Toast.makeText(this@ActivityAdd, "Transaction updated!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                finish()
            }else{
                Toast.makeText(this, "Please add empty fields", Toast.LENGTH_SHORT).show()
            }

        }

        spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                btnDel.visibility = View.GONE
                tvCategoryValue.setText("")
                type = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?){}
            }
        }

    private fun setUpSpinner(spinnerType: Spinner) {
        val items = arrayOf("Expense", "Income")
        val adapter = ArrayAdapter(this,R.layout.spinner_type_add_activtiy, items)
        spinnerType.setSelection(0)
        adapter.setDropDownViewResource(R.layout.spinner_type_add_activtiy)
        spinnerType.adapter = adapter
    }

    private fun convertDateToMillis(date: String): Long {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.UK)
        return (format.parse(date).time)
    }

    private fun showPopGridView(tvCategoryValue: TextView, type: Int, btnSave: Button) {

        val inflater = LayoutInflater.from(this)
        val layout = inflater.inflate(R.layout.popup_grid, null)

        Log.d("ActivityAdd", "showPopGridView: before gridview")

        val gridView : GridView = layout.findViewById(R.id.gridItem)

        Log.d("ActivityAdd", "showPopGridView: after categoriesExpense")

        val adapter = ArrayAdapter(this, R.layout.grid_item,R.id.gridText,
            if(type==0) categoriesExpense else categoriesIncome)
        gridView.adapter = adapter

        val popupWindow = PopupWindow(layout,WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,true)
        popupWindow.showAsDropDown(btnSave,0, 30)

        gridView.setOnItemClickListener { _, _, position, _ ->
            tvCategoryValue.text =
                if(type==0) categoriesExpense[position]
                else categoriesIncome[position]
            popupWindow.dismiss()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Handle the back button click
                onBackPressedDispatcher.onBackPressed()  // Go back to the previous activity
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
