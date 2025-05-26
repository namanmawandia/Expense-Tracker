package com.example.expensetracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import java.time.Instant
import java.time.ZoneId


class DailyFragment: Fragment(R.layout.fragment_daily){
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DailyAdapter
    private lateinit var viewModel: TransactionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_daily, container, false)
        recyclerView = view.findViewById(R.id.dailyRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        Log.d("DailyFragment", "onCreateView: onCreateView completed")

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[TransactionViewModel::class.java]

        val tvNoTransac : TextView = view.findViewById(R.id.tvNoTransac)

        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            if(transactions.isNullOrEmpty()) tvNoTransac.visibility = View.VISIBLE
            else    tvNoTransac.visibility =View.GONE
            val dayTransactionList = groupTransactionsByDay(transactions)
            Log.d("DailyFragment", "onCreateView: "+ dayTransactionList)
            adapter = DailyAdapter(dayTransactionList)
            recyclerView.adapter = adapter
        }
        Log.d("DailyFragment", "onCreateView: onViewCreated completed")
    }

    private fun groupTransactionsByDay(transactions: List<Transaction>): List<DayTransactions> {
        val grouped = transactions.groupBy {
            Instant.ofEpochMilli(it.date).atZone(ZoneId.systemDefault()).toLocalDate()
        }

        Log.d("DailyFragment", "onCreateView: "+ grouped)
        val transList = grouped.map { (date, list) ->
            val dayStartMillis = date.atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli()
            DayTransactions(date = dayStartMillis, transactions = list)
        }.sortedByDescending { it.date }

        Log.d("DailyFragment", "onCreateView: List By day"+ transList)
        return transList
    }

    override fun onResume() {
        super.onResume()
        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            Log.d("DailyFragment", "onResume observer: ${transactions.size}")
            val dayTransactionList = groupTransactionsByDay(transactions)
            adapter = DailyAdapter(dayTransactionList)
            recyclerView.adapter = adapter
        }
    }
}
