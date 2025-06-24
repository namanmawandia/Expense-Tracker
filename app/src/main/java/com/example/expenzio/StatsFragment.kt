package com.example.expenzio

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import java.util.Calendar

class StatsFragment(typeFr: Int) : Fragment() {

    private lateinit var adapter: StatsAdapter
    private lateinit var viewModel: TransactionViewModel
    private lateinit var myViewModel: MonthYearViewModel
    private val typeFragment :Int = typeFr
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvNoDataRV: TextView
    private lateinit var tvNoDataPie: TextView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.expense_fragment, container, false)

        viewModel = ViewModelProvider(requireActivity())[TransactionViewModel::class.java]
        myViewModel = ViewModelProvider(requireActivity())[MonthYearViewModel::class.java]

        recyclerView = view.findViewById(R.id.rvStats)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pieChart = view.findViewById<PieChart>(R.id.pieChart)
        tvNoDataPie = view.findViewById(R.id.tvNoDataPie)
        tvNoDataRV = view.findViewById(R.id.tvNoDataRV)

        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            updateChart(transactions,(myViewModel.selectedMonth.value ?: 0),
                (myViewModel.selectedYear.value ?: 0) + 2010,pieChart)
        }

        myViewModel.selectedMonth.observe(viewLifecycleOwner){month ->
            val transactions : List<Transaction> = viewModel.transactions.value ?: emptyList()
            Log.d("ExpenseFragment", "onViewCreated: month observer "+ month)
            updateChart(transactions,month,(myViewModel.selectedYear.value ?: 0) + 2010, pieChart)
        }
        myViewModel.selectedYear.observe(viewLifecycleOwner){year ->
            val transactions : List<Transaction> = viewModel.transactions.value ?: emptyList()
            Log.d("ExpenseFragment", "onViewCreated: year observer ")
            updateChart(transactions,(myViewModel.selectedMonth.value ?: 0),year + 2010, pieChart)
        }

        val gestureDetector = GestureDetector(requireContext(),
            SwipeGestureListener(requireContext(),
                onSwipeLeft = {
                    SwipeAnimator.animateSwipe(context = requireContext(),targetView =  view,
                        direction = SwipeAnimator.Direction.LEFT) {
                        var month = myViewModel.selectedMonth.value?:0
                        var year = myViewModel.selectedYear.value?:0
                        if(month==11) { month = 0 ;year++ } else month++
                        myViewModel.updateMonthYear(month,year)
                        (activity as? StatsActivity)?.setGlobalMonthYear(myViewModel)
                    }
                },
                onSwipeRight = {
                    SwipeAnimator.animateSwipe(context = requireContext(),targetView =  view,
                        direction = SwipeAnimator.Direction.RIGHT){
                        var month = myViewModel.selectedMonth.value ?: 0
                        var year = myViewModel.selectedYear.value ?: 0
                        if (month == 0) {
                            month = 11;year--
                        } else month--
                        myViewModel.updateMonthYear(month, year)
                        (activity as? StatsActivity)?.setGlobalMonthYear(myViewModel)
                    }
                }
            )
        )

        recyclerView.setOnTouchListener { _, event ->
            Log.d("ExpenseFragment", "Touch event: ${event.action}")
            gestureDetector.onTouchEvent(event)
            true
        }
        tvNoDataPie.setOnTouchListener { _, event ->
            Log.d("ExpenseFragment", "Touch event: ${event.action}")
            gestureDetector.onTouchEvent(event)
            true
        }
        tvNoDataRV.setOnTouchListener { _, event ->
            Log.d("ExpenseFragment", "Touch event: ${event.action}")
            gestureDetector.onTouchEvent(event)
            true
        }

    }

    private fun updateChart(transactions: List<Transaction>, month: Int, year: Int, pieChart: PieChart) {
        val monthFilter = transactions.filter {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it.date
            calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) == (month)
                    && it.type == typeFragment
        }
        val categoryMapAmount:Map<Int, List<Transaction>> = monthFilter.groupBy { it.category }
        Log.d("ExpenseFragment", "updateChart: Map: "+categoryMapAmount)
        val entries = categoryMapAmount.map { (categoryId, transactions) ->
            val totalAmount = transactions.sumOf { it.amount }
            PieEntry(totalAmount.toFloat(),
                if (typeFragment==0) categoriesExpense[categoryId]
                else categoriesIncome[categoryId])
        }
        if(monthFilter.isEmpty())
        {
            tvNoDataPie.visibility = View.VISIBLE
            tvNoDataRV.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            pieChart.visibility = View.GONE
        }
        else{
            tvNoDataPie.visibility = View.GONE
            tvNoDataRV.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            pieChart.visibility = View.VISIBLE
        }


        val colr = mutableListOf<Int>()
        colr.addAll(ColorTemplate.MATERIAL_COLORS.toList())
        colr.addAll(ColorTemplate.COLORFUL_COLORS.toList())
        val dataSet = PieDataSet(entries,"").apply {
            colors = colr
            valueTextSize = 10f
            valueTextColor = Color.BLACK
            setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE)
            setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE)
            sliceSpace = 1f
            valueLinePart1Length = 0.4f
            valueLinePart2Length = 0.6f
            valueLineColor = Color.BLACK
        }
        val pieData = PieData(dataSet)

        pieChart.apply {
            data = pieData;
            description.isEnabled = false
            isDrawHoleEnabled = true
            setUsePercentValues(true)
            setEntryLabelColor(Color.BLACK)
            animateY(600)
            legend.isEnabled = false
            pieChart.setExtraOffsets(45f, 10f, 45f, 10f)
            invalidate()
        }

        val catIdAmountPairs: List<Pair<Int, Double>> = categoryMapAmount.map { (categoryId, transactions) ->
            categoryId to transactions.sumOf { it.amount }
        }.sortedByDescending { it.second }
        Log.d("StatsFragment", "updateChart: CatList size" + catIdAmountPairs)
        adapter = StatsAdapter(catIdAmountPairs,colr,typeFragment)
        recyclerView.adapter = adapter

    }


}