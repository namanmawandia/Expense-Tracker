package com.example.expensetracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
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

    override fun onBindViewHolder(holder: TransactionDayViewHolder, position: Int) {
        val item = transactions[position]
        holder.tvCategory.text = categoriesExpense[item.category]
        holder.tvNote.text = if (item.note.length > 10) {
            item.note.take(10) + "..."
        } else {
            item.note
        }

        holder.tvAmount.text = "%.1f".format(item.amount)
        if(item.type==1)
            holder.tvAmount.setTextColor(ContextCompat.getColor(holder.tvAmount.context,
                R.color.highlightPurple))
    }


    override fun getItemCount(): Int = transactions.size
}