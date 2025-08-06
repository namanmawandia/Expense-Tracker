package com.MStudios.expenzio

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import java.util.Calendar


class StatsActivity : AppCompatActivity(){

    val monthSpinner: Spinner by lazy { findViewById(R.id.monthSpinner) }
    val yearSpinner: Spinner by lazy { findViewById(R.id.yearSpinner) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_stats)

        val tvStatsExpense = findViewById<TextView>(R.id.tvStatsExpense)
        val tvStatsIncome = findViewById<TextView>(R.id.tvStatsIncome)
        val lnrLayoutTransac = findViewById<LinearLayout>(R.id.lnrLayoutTransac)
        val ivLeftArrow = findViewById<ImageView>(R.id.ivLeftArrow)
        val ivRightArrow = findViewById<ImageView>(R.id.ivRightArrow)
        val advStatsBanner = findViewById<AdView>(R.id.advStatsBanner)
        val lnrLayoutTranStat = findViewById<LinearLayout>(R.id.lnrLayoutTranStat)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(lnrLayoutTranStat) { view, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = systemInsets.bottom)
            insets
        }
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

        windowInsetsController.isAppearanceLightStatusBars = true

        // may or may not remove for production, will show test id to me and live to others
        val config = RequestConfiguration.Builder()
//            .setTestDeviceIds(listOf("B224DD7054540A29EE2E104A3AA71A4D"))
            .build()
        MobileAds.setRequestConfiguration(config)

        advStatsBanner.adListener = object : AdListener() {
            override fun onAdFailedToLoad(error: LoadAdError) {
                Log.d("AdMob", "Ad failed: ${error.message}")
            }
        }

        // initializing the bottom Banner ad
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        advStatsBanner.loadAd(adRequest)

        val myViewModel : MonthYearViewModel by viewModels()
        setupSpinner(monthSpinner,yearSpinner,myViewModel)
        highlightSelectedFrag(R.id.tvStatsExpense)
        highlightSelectedTab(R.id.ivStats)
        replaceFragment(StatsFragment(0))

        tvStatsExpense.setOnClickListener{
            replaceFragment(StatsFragment(0))
            highlightSelectedFrag(R.id.tvStatsExpense)
        }
        tvStatsIncome.setOnClickListener{
            replaceFragment(StatsFragment(1))
            highlightSelectedFrag(R.id.tvStatsIncome)
        }
//        ivTransac.setOnClickListener{
        lnrLayoutTransac.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(this, 0, 0)
            finish()
            startActivity(intent, options.toBundle())
        }

        setupArrowSpinner(myViewModel, ivLeftArrow, ivRightArrow)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@StatsActivity, MainActivity::class.java)
                val options = ActivityOptions.makeCustomAnimation(this@StatsActivity, 0, 0)
                finish()
                startActivity(intent, options.toBundle())
            }
        })

    }

    private fun setupArrowSpinner(myViewModel: MonthYearViewModel, ivLeftArrow: ImageView, ivRightArrow: ImageView)
    {
        monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                myViewModel.updateMonthYear(position, myViewModel.selectedYear.value ?: 0)
                setGlobalMonthYear(myViewModel)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                myViewModel.updateMonthYear(myViewModel.selectedMonth.value ?: 0, position)
                setGlobalMonthYear(myViewModel)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        ivLeftArrow.setOnClickListener {
            var month = myViewModel.selectedMonth.value ?: 0
            var year = myViewModel.selectedYear.value ?: 0
            if (month == 0) { month = 11;year-- } else month--
            myViewModel.updateMonthYear(month, year)
            setGlobalMonthYear(myViewModel)
        }
        ivRightArrow.setOnClickListener {
            var month = myViewModel.selectedMonth.value ?: 0
            var year = myViewModel.selectedYear.value ?: 0
            if (month == 11) { month = 0;year++ } else month++
            myViewModel.updateMonthYear(month, year)
            setGlobalMonthYear(myViewModel)
        }
    }

    private fun setupSpinner(monthSpinner: Spinner, yearSpinner: Spinner,
                     myViewModel: MonthYearViewModel) {

        val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct",
            "Nov", "Dec")
        val years = (2010..2040).toList()

        val monthAdapter = ArrayAdapter(this, R.layout.spinner_item_bar_layout, months)
        val yearAdapter = ArrayAdapter(this, R.layout.spinner_item_bar_layout, years)

        val cal = Calendar.getInstance()
        myViewModel.updateMonthYear(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR) - 2010)

        monthAdapter.setDropDownViewResource(R.layout.spinner_item_bar_layout)
        yearAdapter.setDropDownViewResource(R.layout.spinner_item_bar_layout)

        monthSpinner.adapter = monthAdapter
        yearSpinner.adapter = yearAdapter
        setGlobalMonthYear(myViewModel)
    }

    fun setGlobalMonthYear(myViewModel: MonthYearViewModel){
        monthSpinner.setSelection(myViewModel.selectedMonth.value ?: 0)
        yearSpinner.setSelection(myViewModel.selectedYear.value ?: 0)
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frgOuter,fragment)
        Log.d("Main Activity", "Calender inside replace fragment: "+ fragment)
        transaction.commit()
    }

    private fun highlightSelectedTab(selectedId: Int) {
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

    private fun highlightSelectedFrag(selectedId: Int){
        val frags = listOf(R.id.tvStatsExpense, R.id.tvStatsIncome)

        for(textId in frags){
            val text = findViewById<TextView>(textId)
            val isSelected = textId == selectedId
            val color = ContextCompat.getColor(this,
                if(isSelected) R.color.negativeTransac else R.color.primaryLight2)
            text.setTextColor(color)
        }
    }
}