package com.example.expenzio

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog

class DayTransactionAdapter(private val transactions: List<Transaction>,
                            private val dialog: BottomSheetDialog? = null):
    RecyclerView.Adapter<DayTransactionAdapter.TransactionDayViewHolder>() {

    class TransactionDayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvNote: TextView = view.findViewById(R.id.tvNote)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val cnstrLyt : ConstraintLayout = view.findViewById(R.id.cnstrtLytItemDay)
        val view :View = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionDayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_day_transaction, parent, false)
        return TransactionDayViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionDayViewHolder, position: Int) {
        val item = transactions[position]
        val layoutParams = holder.cnstrLyt.layoutParams as ViewGroup.MarginLayoutParams
        if(position==0)
            layoutParams.topMargin = 5
        holder.tvCategory.text = if(item.type==0) categoriesExpense[item.category]
                                else categoriesIncome[item.category]
        holder.tvNote.text = if (item.note.length > 10) { item.note.take(10) + "..." }
                            else { item.note }
        holder.tvAmount.text = "%.1f".format(item.amount)
        if(item.type==1)
            holder.tvAmount.setTextColor(ContextCompat.getColor(holder.tvAmount.context,
                R.color.positiveTransac))

        holder.itemView.setOnClickListener{
            val context = holder.itemView.context
            dialog?.dismiss()
            val intent = Intent(context, ActivityAdd::class.java)
            intent.putExtra("dayTransacAdapter", item.id)
            context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int = transactions.size
}