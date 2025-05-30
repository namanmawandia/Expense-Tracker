package com.example.expensetracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterCalenderRV(private val days: List<CalendarDay>, private val itemHeight: Int) :
    RecyclerView.Adapter<AdapterCalenderRV.DayViewHolder>() {

    class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvExpense: TextView = view.findViewById(R.id.tvExpense)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day_calender,
            parent, false)
        view.layoutParams.height = itemHeight
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]
        holder.tvDate.text = day.date
        holder.tvExpense.text = day.expense?.let { "$it" } ?: ""
    }

    override fun getItemCount(): Int = days.size
}
