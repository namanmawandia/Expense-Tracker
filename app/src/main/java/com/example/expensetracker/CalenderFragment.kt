package com.example.expensetracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

class CalenderFragment: Fragment(){
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterCalenderRV
    private lateinit var viewModel: TransactionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calender, container, false)

        recyclerView = view.findViewById(R.id.calendarRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 7)
        adapter = AdapterCalenderRV(emptyList())
        recyclerView.adapter = adapter

        Log.d("CalenderFragment", "onCreateView: initialization")

        viewModel = ViewModelProvider(requireActivity())[TransactionViewModel::class.java]
        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            // This block runs when data is ready
            val calendarData = generateCalendarDays(transactions, Calendar.YEAR, Calendar.MONTH)
            adapter = AdapterCalenderRV(calendarData)
            recyclerView.adapter = adapter
        }
        Log.d("CalenderFragment", "onCreateView: observe")
        viewModel.getAllTransactions()
        Log.d("CalenderFragment", "onCreateView: get all transaction")
        return view
    }

    fun generateCalendarDays(transactions:List<Transaction>,year: Int, month: Int): List<CalendarDay> {
        val calendarDays = mutableListOf<CalendarDay>()
        val calendar = Calendar.getInstance()

        val monthTransactions = transactions.filter {
            calendar.timeInMillis = it.date
            calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) == (month - 1)
        }
        Log.d("CalenderFragment", "generateCalendarDays: monthFilter")

        val expenseByDay: Map<Int, Double> = monthTransactions.groupBy {
            calendar.timeInMillis = it.date
            calendar.get(Calendar.DAY_OF_MONTH)
        }.mapValues { entry ->
            entry.value.sumOf { it.amount }
        }
        Log.d("CalenderFragment", "generateCalendarDays: expenseByDay")

        // Set calendar to first day of the month
        calendar.set(year, month - 1, 1)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 7 = Saturday
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Add blank days before 1st of the month
        for (i in 1 until firstDayOfWeek) {
            calendarDays.add(CalendarDay("")) // Empty day
        }
        Log.d("CalenderFragment", "generateCalendarDays: extra days added in start")
        // Add actual days
        for (day in 1..daysInMonth) {
            val expense = expenseByDay[day]
            calendarDays.add(
                CalendarDay(
                    date = day.toString(),
                    expense = expense
                )
            )
        }
        Log.d("CalenderFragment", "generateCalendarDays: Days updated with value")

        // Optional: pad the end to make the total count a multiple of 7
        while (calendarDays.size % 7 != 0) {
            calendarDays.add(CalendarDay(""))
        }
        Log.d("CalenderFragment", "generateCalendarDays: extra days added in end")
        return calendarDays
    }


}