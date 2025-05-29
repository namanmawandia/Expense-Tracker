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
    private lateinit var myViewModel: MonthYearViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calender, container, false)

        recyclerView = view.findViewById(R.id.calendarRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 7)

        Log.d("CalenderFragment", "onCreateView: initialization")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[TransactionViewModel::class.java]
        myViewModel = ViewModelProvider(requireActivity())[MonthYearViewModel::class.java]

        recyclerView.post {
            viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
                updateCalender(transactions,(myViewModel.selectedMonth.value ?: 0),
                    (myViewModel.selectedYear.value ?: 0) + 2010)
            }
            myViewModel.selectedMonth.observe(viewLifecycleOwner){month ->
                val transactions : List<Transaction> = viewModel.transactions.value ?: emptyList()
                Log.d("CalenderFragment", "onViewCreated: month observer "+ month)
                updateCalender(transactions,month,(myViewModel.selectedYear.value ?: 0) + 2010)
            }
            myViewModel.selectedYear.observe(viewLifecycleOwner){year ->
                val transactions : List<Transaction> = viewModel.transactions.value ?: emptyList()
                Log.d("CalenderFragment", "onViewCreated: inside observer transaction ")
                updateCalender(transactions,(myViewModel.selectedMonth.value ?: 0),year + 2010)
            }
        }
    }

    private fun updateCalender(transactions: List<Transaction>, month: Int, year: Int) {
        val calendarData = generateCalendarDays(transactions, year, month)
        Log.d("CalenderFragment", "updateCalender: inside uodateCalender " +month)
        adapter = AdapterCalenderRV(calendarData, recyclerView.height/6)
        recyclerView.adapter = adapter
    }


    private fun generateCalendarDays(transactions:List<Transaction>,year: Int, month: Int): List<CalendarDay> {
        val calendarDays = mutableListOf<CalendarDay>()

        Log.d("CalenderFragment", "generateCalendarDays: month"+ (month)+" "+ year)
        val monthTransactions = transactions.filter {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it.date
            calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) == (month)
        }
        Log.d("CalenderFragment", "generateCalendarDays: monthFilter")

        val expenseByDay: Map<Int, Double> = monthTransactions.groupBy {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it.date
            calendar.get(Calendar.DAY_OF_MONTH)
        }.mapValues { entry ->
            entry.value.sumOf { it.amount }
        }
        Log.d("CalenderFragment", "generateCalendarDays: expenseByDay")

        val calendar = Calendar.getInstance()
        // Set calendar to first day of the month
        calendar.set(year, month, 1)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 7 = Saturday
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Add blank days before 1st of the month
        for (i in 1 until firstDayOfWeek) {
            calendarDays.add(CalendarDay(""))
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

        // pad the end to make the total count a multiple of 7
        while (calendarDays.size % 7 != 0) {
            calendarDays.add(CalendarDay(""))
        }
        Log.d("CalenderFragment", "generateCalendarDays: extra days added in end")
        return calendarDays
    }

    override fun onResume() {
        super.onResume()

        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            updateCalender(transactions,(myViewModel.selectedMonth.value ?: 0),
                (myViewModel.selectedYear.value ?: 0) + 2010)
        }
    }

}