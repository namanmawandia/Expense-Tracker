package com.example.expensetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[TransactionViewModel::class.java]

        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            val dayTransactionList = groupTransactionsByDay(transactions)
            adapter = DailyAdapter(dayTransactionList)
            recyclerView.adapter = adapter
        }
    }

    private fun groupTransactionsByDay(transactions: List<Transaction>): List<DayTransactions> {
        val grouped = transactions.groupBy {
            Instant.ofEpochMilli(it.date).atZone(ZoneId.systemDefault()).toLocalDate()
        }

        return grouped.map { (date, list) ->
            val dayStartMillis = date.atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli()
            DayTransactions(date = dayStartMillis, transactions = list)
        }.sortedByDescending { it.date }
    }
}
