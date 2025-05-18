package com.example.expensetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

class CalenderFragment: Fragment(){
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterCalenderRV

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calender, container, false)

        recyclerView = view.findViewById(R.id.calendarRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 7)
        adapter = AdapterCalenderRV(generateCalendarDays(Calendar.YEAR, Calendar.MONTH))
        recyclerView.adapter = adapter

        return view
    }

    fun generateCalendarDays(year: Int, month: Int): List<CalendarDay> {
        val calendarDays = mutableListOf<CalendarDay>()
        val calendar = Calendar.getInstance()

        // Set calendar to first day of the month
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1) // Calendar.MONTH is 0-based
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 7 = Saturday
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Add blank days before 1st of the month
        for (i in 1 until firstDayOfWeek) {
            calendarDays.add(CalendarDay("")) // Empty day
        }

        // Add actual days with sample/mock data
        for (day in 1..daysInMonth) {
            // You can later replace this part with your real data lookup
            val expense = if() else null

            calendarDays.add(
                CalendarDay(
                    date = day.toString(),
                    expense = expense,
                )
            )
        }

        // Optional: pad the end to make the total count a multiple of 7
        while (calendarDays.size % 7 != 0) {
            calendarDays.add(CalendarDay(""))
        }

        return calendarDays
    }


}