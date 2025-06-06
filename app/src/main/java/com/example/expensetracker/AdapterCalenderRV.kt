package com.example.expensetracker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterCalenderRV(private val days: List<CalendarDay>, private val itemHeight: Int,
                        private val listener: OnItemClickListenerDay) :
    RecyclerView.Adapter<AdapterCalenderRV.DayViewHolder>() {

    interface OnItemClickListenerDay {
        fun onItemClick(item: CalendarDay, parent: Context)
    }

    inner class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvExpense: TextView = view.findViewById(R.id.tvExpense)
        val tvIncome : TextView = view.findViewById(R.id.tvIncome)
        val tvTotalAmount : TextView = view.findViewById(R.id.tvTotalAmount)


        fun bind(item: CalendarDay, parent: Context) {
            itemView.setOnClickListener {
                listener.onItemClick(item,parent)
            }
        }
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
        holder.tvExpense.text = if (day.expense != null && day.expense != 0.0) "${day.expense}" else ""
        holder.tvIncome.text = if (day.income != null && day.income != 0.0) "${day.income}" else ""
        holder.tvTotalAmount.text = if(day.total!=null &&
            (day.total!= 0.0 || day.expense!= 0.0 || day.income != 0.0))
            "${day.total}" else ""
        holder.bind(days[position],holder.itemView.context)
    }

    override fun getItemCount(): Int = days.size
}
