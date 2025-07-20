package com.MStudios.expenzio

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.PopupWindow
import androidx.appcompat.widget.Toolbar
import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import java.text.SimpleDateFormat
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import java.text.ParseException
import java.util.*


val catExpenseToNum : Map<String,Int> = mapOf("🍔 Food" to 0, "🚕 Transport" to 1,
    "\uD83E\uDDCD\u200D♂\uFE0F Personal" to 2, "\uD83E\uDE7A Medical" to 3,
    "🏠 Household" to 4, "🎓 Education" to 5)
val categoriesExpense = arrayOf("🍔 Food", "🚕 Transport", "\uD83E\uDDCD\u200D♂\uFE0F Personal",
    "\uD83E\uDE7A Medical", "🏠 Household", "🎓 Education")
val catIncomeToNum : Map<String,Int> = mapOf("\uD83D\uDCB5 Wages" to 0, "\uD83D\uDCB0 Salary" to 1,
    "\uD83D\uDCB8 Commissions" to 2, "\uD83E\uDE99 Tips" to 3, "\uD83C\uDF89 Bonus" to 4,
    "\uD83D\uDC68\uD83C\uDFFD\u200D\uD83D\uDCBB Freelancing" to 5)
val categoriesIncome = arrayOf("\uD83D\uDCB5 Wages","\uD83D\uDCB0 Salary","\uD83D\uDCB8 Commissions",
    "\uD83E\uDE99 Tips", "\uD83C\uDF89 Bonus", "\uD83D\uDC68\uD83C\uDFFD\u200D\uD83D\uDCBB Freelancing")

class ActivityAdd : AppCompatActivity() {

    private lateinit var viewModel: TransactionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add)

        val toolbar : Toolbar = findViewById(R.id.toolbar)
        val etDate : EditText = findViewById(R.id.etDate)
        val etAmount : EditText = findViewById(R.id.etAmount)
        val etNote : EditText = findViewById(R.id.etNote)
        val etCategory : EditText = findViewById(R.id.etCategory)
        val btnSave : Button = findViewById(R.id.btnSave)
        val spinnerType : Spinner = findViewById(R.id.typeSpinner)
        val btnDel : Button = findViewById(R.id.btnDelete)
        val selectedColor = ContextCompat.getColor(this, R.color.negativeTransac)
        val advAddBanner = findViewById<AdView>(R.id.advAddBanner)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = true

        // may or may not remove for production, will show test id to me and live to others
        val config = RequestConfiguration.Builder()
            .setTestDeviceIds(listOf("B224DD7054540A29EE2E104A3AA71A4D"))
            .build()
        MobileAds.setRequestConfiguration(config)

        // initializing the bottom Banner ad
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        advAddBanner.loadAd(adRequest)

        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
        val transactionViewModel : TransactionViewModel by viewModels()
        var type = 0 // for expense or income

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        setUpSpinner(spinnerType)

        val delPresent = intent.getBooleanExtra("main", false)// true-> main
        val idTransaction = intent.getIntExtra("dayTransacAdapter",-1)

        if(delPresent) {
            btnDel.visibility = View.GONE
        }
        else{
            btnSave.setText("Update")
            val cal = Calendar.getInstance()
            lifecycleScope.launch {
                val oldTra = viewModel.getTransactionById(idTransaction)
                cal.timeInMillis = oldTra.date
                etAmount.setText(oldTra.amount.toString())
                etNote.setText(oldTra.note)
                val setDay = cal.get(Calendar.DAY_OF_MONTH)
                val setMonth = cal.get(Calendar.MONTH)
                val setYear = cal.get(Calendar.YEAR)
                val formattedDate = String.format("%02d/%02d/%d", setDay, setMonth + 1, setYear)
                etDate.setText(formattedDate)
                spinnerType.setSelection(oldTra.type)
                etCategory.setText( if (oldTra.type == 0) categoriesExpense[oldTra.category]
                                else categoriesIncome[oldTra.category])
            }
        }

        val initDate = intent.getIntExtra("Date",1)
        val initMonth = intent.getIntExtra("Month",1)
        val initYear = intent.getIntExtra("Year",2010)

        if(delPresent)
            etDate.setText(String.format("%02d/%02d/%d", initDate, initMonth, initYear))

        etCategory.setOnClickListener{
            val isNowSelected = !etCategory.isSelected
            etCategory.isSelected = isNowSelected
            etCategory.setOnFocusChangeListener { _, hasFocus ->
                etCategory.backgroundTintList = ColorStateList.valueOf(
                    if (hasFocus) selectedColor else Color.BLACK)
            }
            btnDel.visibility = View.GONE
            showBottomUp(etCategory,type)
//            showPopGridView(etCategory,type,btnSave)
        }

        btnDel.setOnClickListener{
            if(idTransaction==-1)
                Toast.makeText(this, "No record present for following", Toast.LENGTH_SHORT).show()
            else {
                viewModel.delWithID(idTransaction)
                Toast.makeText(this, "Transaction Deleted", Toast.LENGTH_SHORT).show()
            }
            finish()
        }

        etDate.setOnClickListener{
            btnDel.visibility = View.GONE
            val isNowSelected = !etDate.isSelected
            etDate.isSelected = isNowSelected
            etDate.setOnFocusChangeListener { _,hasFocus ->
                etDate.backgroundTintList = ColorStateList.valueOf(
                    if (hasFocus) selectedColor else Color.BLACK)
            }
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

        etAmount.setOnFocusChangeListener { _, hasFocus ->
            val isNowSelected = !etAmount.isSelected
            etAmount.isSelected = isNowSelected
            if(hasFocus){
                etAmount.backgroundTintList = ColorStateList.valueOf(selectedColor)
                btnDel.visibility = View.GONE
            }
            else
                etAmount.backgroundTintList = ColorStateList.valueOf(Color.BLACK)
        }

        etNote.setOnFocusChangeListener { _, hasFocus ->
            val isNowSelected = !etNote.isSelected
            etNote.isSelected = isNowSelected
            if(hasFocus){
                etNote.backgroundTintList = ColorStateList.valueOf(selectedColor)
                btnDel.visibility = View.GONE
            }
            else
                etNote.backgroundTintList = ColorStateList.valueOf(Color.BLACK)
        }

        btnSave.setOnClickListener{
            val amt = etAmount.text.toString().toDoubleOrNull()
            val note = etNote.text.toString()
            val cat = if(type==0) catExpenseToNum[etCategory.text.toString()]
                else catIncomeToNum[etCategory.text.toString()]
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

        var isTouched=false;
        spinnerType.setOnTouchListener{_,_,->
            btnDel.visibility = View.GONE
            isTouched = true
            false
        }

        spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if(delPresent || isTouched){
                    etCategory.setText("")
                    isTouched = false
                }
                type = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?){}
        }
    }

    private fun showBottomUp(etCategory: EditText, type: Int) {
        val view = LayoutInflater.from(this).inflate(R.layout.popup_grid,null)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(view) { view, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = systemInsets.bottom)
            insets
        }

        val gridView : GridView = view.findViewById(R.id.gridItem)

        Log.d("ActivityAdd", "showPopGridView: after categoriesExpense")

        val adapter = ArrayAdapter(this, R.layout.grid_item,R.id.gridText,
            if(type==0) categoriesExpense else categoriesIncome)
        gridView.adapter = adapter

        view.post {
            setGridViewHeightBasedOnChildren(gridView)
        }

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view)

        // for full categiry expansion in landscape view
        val parent = view.parent as View
        val params = parent.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior as BottomSheetBehavior<View>
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        dialog.show()

        gridView.setOnItemClickListener { _, _, position, _ ->
            etCategory.setText(
                if(type==0) categoriesExpense[position]
                else categoriesIncome[position])
            dialog.dismiss()
        }

    }

    fun setGridViewHeightBasedOnChildren(gridView: GridView) {
        val listAdapter = gridView.adapter ?: return

        val rows = Math.ceil(listAdapter.count / 2.0).toInt() // 2 columns

        var totalHeight = 0
        for (i in 0 until rows) {
            val listItem = listAdapter.getView(i, null, gridView)
            listItem.measure(
                View.MeasureSpec.makeMeasureSpec(gridView.width, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.UNSPECIFIED
            )
            totalHeight += listItem.measuredHeight
        }

        totalHeight += (gridView.verticalSpacing * (rows - 1))

        val params = gridView.layoutParams
        params.height = totalHeight
        gridView.layoutParams = params
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

    private fun showPopGridView(tvCategoryValue: EditText, type: Int, btnSave: Button) {

        val inflater = LayoutInflater.from(this)
        val layout = inflater.inflate(R.layout.popup_grid, null)

        val gridView : GridView = layout.findViewById(R.id.gridItem)

        Log.d("ActivityAdd", "showPopGridView: after categoriesExpense")

        val adapter = ArrayAdapter(this, R.layout.grid_item,R.id.gridText,
            if(type==0) categoriesExpense else categoriesIncome)
        gridView.adapter = adapter

        val popupWindow = PopupWindow(layout,WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,true)
        popupWindow.showAsDropDown(btnSave,0, 20)

        gridView.setOnItemClickListener { _, _, position, _ ->
            tvCategoryValue.setText(
                if(type==0) categoriesExpense[position]
                else categoriesIncome[position])
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
