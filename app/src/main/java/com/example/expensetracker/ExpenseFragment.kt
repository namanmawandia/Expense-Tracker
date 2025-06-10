package com.example.expensetracker

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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

class ExpenseFragment : Fragment() {

    private lateinit var adapter: ExpenseAdapter
    private lateinit var viewModel: TransactionViewModel
    private lateinit var myViewModel: MonthYearViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.expense_fragment, container, false)

        viewModel = ViewModelProvider(requireActivity())[TransactionViewModel::class.java]
        myViewModel = ViewModelProvider(requireActivity())[MonthYearViewModel::class.java]

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pieChart = view.findViewById<PieChart>(R.id.pieChart)
        updateChart(viewModel.transactions.value?: emptyList(),(myViewModel.selectedMonth.value ?: 0),
                  (myViewModel.selectedYear.value ?: 0) + 2010,pieChart)

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
                        (activity as? MainActivity)?.setGlobalMonthYear(myViewModel)
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
                        (activity as? MainActivity)?.setGlobalMonthYear(myViewModel)
                    }
                }
            )
        )

        view.setOnTouchListener { _, event ->
            Log.d("ExpenseFragment", "Touch event: ${event.action}")
            gestureDetector.onTouchEvent(event)
            false
        }

    }

    private fun updateChart(transactions: List<Transaction>, month: Int, year: Int, pieChart: PieChart) {
        val monthFilter = transactions.filter {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it.date
            calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) == (month)
                    && it.type==0
        }
        val categoryMapAmount:Map<Int, List<Transaction>> = monthFilter.groupBy { it.category }
        Log.d("ExpenseFragment", "updateChart: Map: "+categoryMapAmount)
        val entries = categoryMapAmount.map { (categoryId, transactions) ->
            val totalAmount = transactions.sumOf { it.amount }
            PieEntry(totalAmount.toFloat(), categoriesExpense[categoryId])
        }

        val dataSet = PieDataSet(entries,"").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextSize = 14f
            valueTextColor = Color.BLACK
        }
        val pieData = PieData(dataSet)

        pieChart.apply {
            data = pieData;
            description.isEnabled = false
            isDrawHoleEnabled = true
            setUsePercentValues(true)
            setEntryLabelColor(Color.BLACK)
            animateY(1000)
            invalidate()
        }

    }


}