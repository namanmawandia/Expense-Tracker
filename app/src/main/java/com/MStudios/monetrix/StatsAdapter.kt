package com.MStudios.monetrix

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class StatsAdapter(
    private var catIdAmnt: List<Pair<Int, Double>>,
    private val color: List<Int>,
    private val typeFragment: Int
) :
    RecyclerView.Adapter<StatsAdapter.StatsViewHolder>()  {

    inner class StatsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvCategory: TextView = view.findViewById(R.id.tvCategory)
        var btncolor: Button = view.findViewById(R.id.btnColor)
        var tvAmount: TextView = view.findViewById(R.id.tvAmount)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.stats_category_item,
            parent, false)
        return StatsViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatsViewHolder, position: Int) {
        holder.tvCategory.text = if (typeFragment==0) categoriesExpense[catIdAmnt[position].first]
            else categoriesIncome[catIdAmnt[position].first]
        holder.tvAmount.text = catIdAmnt[position].second.toString()
        Log.d("StatsAdapter", "onBindViewHolder: " +color)
        holder.btncolor.setBackgroundColor(color[position])
        val percent = catIdAmnt[position].second*100.0/catIdAmnt.sumOf { it.second }
        holder.btncolor.setText(String.format("%.1f",percent)+"%")
    }

    override fun getItemCount(): Int = catIdAmnt.size
}