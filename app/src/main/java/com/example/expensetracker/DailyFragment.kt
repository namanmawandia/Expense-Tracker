package com.example.expensetracker

import SwipeGestureListener
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.Instant
import java.time.ZoneId


class DailyFragment: Fragment(R.layout.fragment_daily){
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DailyAdapter
    private lateinit var viewModel: TransactionViewModel
    private lateinit var myViewModel: MonthYearViewModel
    private lateinit var tvNoTransac : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_daily, container, false)
        recyclerView = view.findViewById(R.id.dailyRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        Log.d("DailyFragment", "onCreateView: onCreateView completed")

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[TransactionViewModel::class.java]
        myViewModel = ViewModelProvider(requireActivity())[MonthYearViewModel::class.java]

        tvNoTransac = view.findViewById(R.id.tvNoTransac)

        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            updateDaily(transactions,(myViewModel.selectedMonth.value ?: 0),
                (myViewModel.selectedYear.value ?: 0) + 2010,tvNoTransac)
        }
        myViewModel.selectedMonth.observe(viewLifecycleOwner){month ->
            val transactions : List<Transaction> = viewModel.transactions.value ?: emptyList()
            updateDaily(transactions, month,
                (myViewModel.selectedYear.value ?: 0) + 2010, tvNoTransac)
        }
        myViewModel.selectedYear.observe(viewLifecycleOwner){year ->
            val transactions : List<Transaction> = viewModel.transactions.value ?: emptyList()
            updateDaily(transactions, (myViewModel.selectedMonth.value ?: 0),
                year + 2010, tvNoTransac)
        }
        Log.d("DailyFragment", "onCreateView: onViewCreated completed")
        
        val gestureDetector = GestureDetector(requireContext(),
            SwipeGestureListener(requireContext(),
                onSwipeLeft = {
                     SwipeAnimator.animateSwipe(context = requireContext(),targetView =  recyclerView,
                         direction = SwipeAnimator.Direction.LEFT){
                        var month = myViewModel.selectedMonth.value?:0
                        var year = myViewModel.selectedYear.value?:0
                        if(month==11) { month = 0 ;year++ } else month++
                        myViewModel.updateMonthYear(month,year)
                        (activity as? MainActivity)?.setGlobalMonthYear(myViewModel)
                    }
                },
                onSwipeRight = {
                    SwipeAnimator.animateSwipe(context = requireContext(),targetView =  recyclerView,
                        direction = SwipeAnimator.Direction.RIGHT) {
                        var month = myViewModel.selectedMonth.value ?: 0
                        var year = myViewModel.selectedYear.value ?: 0
                        if (month == 0) { month = 11;year-- } else month--
                        myViewModel.updateMonthYear(month, year)
                        (activity as? MainActivity)?.setGlobalMonthYear(myViewModel)
                    }
                }
            )
        )

        recyclerView.setOnTouchListener { _, event ->
            Log.d("CalenderFragment", "Touch event: ${event.action}")
            gestureDetector.onTouchEvent(event)
            false
        }
    }

    private fun updateDaily(
        transactions: List<Transaction>,
        month: Int,
        year: Int,
        tvNoTransac: TextView
    ){
        val dayTransactionList = groupTransactionsByDay(transactions,month,year)
        Log.d("DailyFragment", "onCreateView: "+ dayTransactionList)
        if(dayTransactionList.isEmpty()) tvNoTransac.visibility = View.VISIBLE
        else tvNoTransac.visibility = View.GONE
        adapter = DailyAdapter(dayTransactionList)
        recyclerView.adapter = adapter
    }

    private fun groupTransactionsByDay(transactions: List<Transaction>, month: Int, year: Int):
            List<DayTransactions> {
        val grouped = transactions
            .map { it to Instant.ofEpochMilli(it.date).atZone(ZoneId.systemDefault()).toLocalDate() }
            .filter { (_, localDate) ->
                localDate.monthValue == month+1 && localDate.year == year
            }
            .groupBy { (_, localDate) -> localDate }
            .mapValues { entry -> entry.value.map { it.first } }

        Log.d("DailyFragment", "onCreateView: "+ grouped)

        val transList = grouped.map { (date, list) ->
            val dayStartMillis = date.atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli()
            DayTransactions(date = dayStartMillis, transactions = list)
        }.sortedByDescending { it.date }

        Log.d("DailyFragment", "onCreateView: List By day"+ transList)
        return transList
    }

    override fun onResume() {
        super.onResume()
        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            updateDaily(transactions, (myViewModel.selectedMonth.value ?: 0),
                (myViewModel.selectedYear.value ?: 0) + 2010, tvNoTransac)
        }
    }
}
