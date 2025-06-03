package com.example.expensetracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.Instant
import java.time.ZoneId

class DailyAdapter(private val dayTransactions: List<DayTransactions>) :
    RecyclerView.Adapter<DailyAdapter.DayViewHolder>() {

    class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvMonthYear: TextView = view.findViewById(R.id.tvMonthYear)
        val tvTotalExpense: TextView = view.findViewById(R.id.tvTotalExpense)
        val tvTotalIncome: TextView = view.findViewById(R.id.tvTotalIncome)
        val tvDay: TextView = view.findViewById(R.id.tvDay)
        val transactionRecycler: RecyclerView = view.findViewById(R.id.rvDailyTransactions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_day_daily, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val item = dayTransactions[position]
        val date = Instant.ofEpochMilli(item.date).atZone(ZoneId.systemDefault()).toLocalDate()
        holder.tvDate.text = date.dayOfMonth.toString()
        holder.tvDay.text = date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
            .substring(0,3)
        holder.tvMonthYear.text = "${date.monthValue.toString().padStart(2, '0')}" +
                ".${date.year}"

        val totalIncome = item.transactions.sumOf { if(it.type==1) it.amount else 0.0 }
        val totalExpense = item.transactions.sumOf {if(it.type==0) it.amount else 0.0}
        holder.tvTotalExpense.text = String.format("%.1f", totalExpense)
        holder.tvTotalIncome.text = String.format("%.1f", totalIncome)
        holder.transactionRecycler.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.transactionRecycler.adapter = DayTransactionAdapter(item.transactions)
    }

    override fun getItemCount(): Int = dayTransactions.size
}
