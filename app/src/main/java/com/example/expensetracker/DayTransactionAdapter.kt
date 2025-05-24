package com.example.expensetracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DayTransactionAdapter(private val transactions: List<Transaction>):
    RecyclerView.Adapter<DayTransactionAdapter.TransactionDayViewHolder>() {

    class TransactionDayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvNote: TextView = view.findViewById(R.id.tvNote)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionDayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_day_transaction, parent, false)
        return TransactionDayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayTransactionAdapter.TransactionDayViewHolder, position: Int) {
        val item = transactions[position]
        holder.tvCategory.text = categories[item.category]
        holder.tvNote.text = item.note
        holder.tvAmount.text = "%.2f".format(item.amount)
    }


    override fun getItemCount(): Int = transactions.size
}