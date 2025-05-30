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
import java.time.Instant
import java.time.ZoneId


class DailyFragment: Fragment(R.layout.fragment_daily){
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DailyAdapter
    private lateinit var viewModel: TransactionViewModel
    private lateinit var myViewModel: MonthYearViewModel
    private lateinit var tvNoTransac : TextView

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
        myViewModel = ViewModelProvider(requireActivity())[MonthYearViewModel::class.java]

        tvNoTransac = view.findViewById(R.id.tvNoTransac)

        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            updateDaily(transactions,(myViewModel.selectedMonth.value ?: 0),
                (myViewModel.selectedYear.value ?: 0) + 2010,tvNoTransac)
        }
        myViewModel.selectedMonth.observe(viewLifecycleOwner){month ->
            val transactions : List<Transaction> = viewModel.transactions.value ?: emptyList()
            updateDaily(transactions, month,
                (myViewModel.selectedYear.value ?: 0) + 2010, tvNoTransac)
        }
        myViewModel.selectedYear.observe(viewLifecycleOwner){year ->
            val transactions : List<Transaction> = viewModel.transactions.value ?: emptyList()
            updateDaily(transactions, (myViewModel.selectedMonth.value ?: 0),
                year + 2010, tvNoTransac)
        }
        Log.d("DailyFragment", "onCreateView: onViewCreated completed")
    }

    private fun updateDaily(
        transactions: List<Transaction>,
        month: Int,
        year: Int,
        tvNoTransac: TextView
    ){
        val dayTransactionList = groupTransactionsByDay(transactions,month,year)
        Log.d("DailyFragment", "onCreateView: "+ dayTransactionList)
        if(dayTransactionList.isEmpty()) tvNoTransac.visibility = View.VISIBLE
        else tvNoTransac.visibility = View.GONE
        adapter = DailyAdapter(dayTransactionList)
        recyclerView.adapter = adapter
    }

    private fun groupTransactionsByDay(transactions: List<Transaction>, month: Int, year: Int):
            List<DayTransactions> {
        val grouped = transactions
            .map { it to Instant.ofEpochMilli(it.date).atZone(ZoneId.systemDefault()).toLocalDate() }
            .filter { (_, localDate) ->
                localDate.monthValue == month+1 && localDate.year == year
            }
            .groupBy { (_, localDate) -> localDate }
            .mapValues { entry -> entry.value.map { it.first } }

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
            updateDaily(transactions, (myViewModel.selectedMonth.value ?: 0),
                (myViewModel.selectedYear.value ?: 0) + 2010, tvNoTransac)
        }
    }
}
