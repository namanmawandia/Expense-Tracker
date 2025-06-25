package com.example.expenzio

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.time.format.TextStyle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Locale


class CalenderFragment: Fragment(), AdapterCalenderRV.OnItemClickListenerDay{
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterCalenderRV
    private lateinit var viewModel: TransactionViewModel
    private lateinit var myViewModel: MonthYearViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calender, container, false)

        recyclerView = view.findViewById(R.id.calendarRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 7)

        Log.d("CalenderFragment", "onCreateView: initialization")
        return view
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[TransactionViewModel::class.java]
        myViewModel = ViewModelProvider(requireActivity())[MonthYearViewModel::class.java]

        recyclerView.post {
            viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
                updateCalender(transactions,(myViewModel.selectedMonth.value ?: 0),
                    (myViewModel.selectedYear.value ?: 0) + 2010)
            }
            myViewModel.selectedMonth.observe(viewLifecycleOwner){month ->
                val transactions : List<Transaction> = viewModel.transactions.value ?: emptyList()
                Log.d("CalenderFragment", "onViewCreated: month observer "+ month)
                updateCalender(transactions,month,(myViewModel.selectedYear.value ?: 0) + 2010)
            }
            myViewModel.selectedYear.observe(viewLifecycleOwner){year ->
                val transactions : List<Transaction> = viewModel.transactions.value ?: emptyList()
                Log.d("CalenderFragment", "onViewCreated: inside observer transaction ")
                updateCalender(transactions,(myViewModel.selectedMonth.value ?: 0),year + 2010)
            }
        }

        val gestureDetector = GestureDetector(requireContext(),
            SwipeGestureListener(requireContext(),
                onSwipeLeft = {
                    SwipeAnimator.animateSwipe(context = requireContext(),targetView =  recyclerView,
                        direction = SwipeAnimator.Direction.LEFT) {
                        var month = myViewModel.selectedMonth.value?:0
                        var year = myViewModel.selectedYear.value?:0
                        if(month==11) { month = 0 ;year++ } else month++
                        myViewModel.updateMonthYear(month,year)
                        (activity as? MainActivity)?.setGlobalMonthYear(myViewModel)
                    }
                },
                onSwipeRight = {
                    SwipeAnimator.animateSwipe(context = requireContext(),targetView =  recyclerView,
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

        recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                gestureDetector.onTouchEvent(e)
                return false
            }
            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })

    }

    override fun onItemClick(item: CalendarDay, parent: Context) {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_popup_whole_day,null)

        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvMonthYear: TextView = view.findViewById(R.id.tvMonthYear)
        val tvTotalExpense: TextView = view.findViewById(R.id.tvTotalExpense)
        val tvTotalIncome: TextView = view.findViewById(R.id.tvTotalIncome)
        val tvDay: TextView = view.findViewById(R.id.tvDay)
        val transacRecycler: RecyclerView = view.findViewById(R.id.rvDailyTransactions)
        val fabAdd: FloatingActionButton = view.findViewById(R.id.fabAdd)

        tvDate.text = item.date
        tvTotalIncome.text = item.income.toString()
        tvTotalExpense.text = item.expense.toString()
        val month = (myViewModel.selectedMonth.value?:0) +1
        val year = (myViewModel.selectedYear.value ?: 0) + 2010
        tvDay.text = LocalDate.of(year,month,item.date?.toInt()?:1).dayOfWeek
            .getDisplayName( TextStyle.SHORT ,Locale.ENGLISH)
        tvMonthYear.text = "${month.toString().padStart(2, '0')}" + ".${year}"

        // finding list of transactions on particular day
        val transactions : List<Transaction> = viewModel.transactions.value ?: emptyList()
        val transacDay : List<Transaction> = transactions.filter { t->
            t.date == (LocalDate.of(year,month,item.date?.toInt()?:1))
                .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }

        // padding needed in bottom pop up to show all the items in recycler view
        transacRecycler.setPadding(
            transacRecycler.paddingLeft,
            transacRecycler.paddingTop,
            transacRecycler.paddingRight,
            150
        )
        transacRecycler.clipToPadding = false

        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(view)
        dialog.show()

        // using same adapter as used by DailyAdapter
        transacRecycler.layoutManager = LinearLayoutManager(requireContext())
        transacRecycler.adapter = DayTransactionAdapter(transacDay,dialog)

        fabAdd.setOnClickListener{
            val intent = Intent(requireContext(),ActivityAdd::class.java)
            intent.putExtra("Date",item.date?.toInt())
            intent.putExtra("Month",month)
            intent.putExtra("Year",year)
            intent.putExtra("main",true)
            dialog.dismiss()
            startActivity(intent)
        }
    }

    private fun updateCalender(transactions: List<Transaction>, month: Int, year: Int) {
        val calendarData = generateCalendarDays(transactions, year, month)
        Log.d("CalenderFragment", "updateCalender: inside uodateCalender " +month)
        adapter = AdapterCalenderRV(calendarData, recyclerView.height/6, this)
        recyclerView.adapter = adapter
    }


    private fun generateCalendarDays(transactions:List<Transaction>,year: Int, month: Int): List<CalendarDay> {
        val calendarDays = mutableListOf<CalendarDay>()

        Log.d("CalenderFragment", "generateCalendarDays: month"+ (month)+" "+ year)
        val monthTransactions = transactions.filter {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it.date
            calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) == (month)
        }
        Log.d("CalenderFragment", "generateCalendarDays: monthFilter")

        val expenseByDay: Map<Int, List<Transaction>> = monthTransactions.groupBy {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it.date
            calendar.get(Calendar.DAY_OF_MONTH)
        }
        Log.d("CalenderFragment", "generateCalendarDays: expenseByDay")

        val calendar = Calendar.getInstance()
        // Set calendar to first day of the month
        calendar.set(year, month, 1)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 7 = Saturday
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Add blank days before 1st of the month
        for (i in 1 until firstDayOfWeek) {
            calendarDays.add(CalendarDay(""))
        }
        Log.d("CalenderFragment", "generateCalendarDays: extra days added in start")
        // Add actual days
        for (day in 1..daysInMonth) {
            val expense = expenseByDay[day].orEmpty().sumOf { if(it.type==0) it.amount else 0.0}
            val income = expenseByDay[day].orEmpty().sumOf { if(it.type==1) it.amount else 0.0}
            val total = income - expense
            calendarDays.add(
                CalendarDay(
                    date = day.toString(), expense = expense, total = total, income = income
                )
            )
        }
        Log.d("CalenderFragment", "generateCalendarDays: Days updated with value")

        // pad the end to make the total count a multiple of 7
        while (calendarDays.size < 42 || calendarDays.size % 7 != 0) {
            calendarDays.add(CalendarDay(""))
        }
        Log.d("CalenderFragment", "generateCalendarDays: extra days added in end")
        return calendarDays
    }

    override fun onResume() {
        super.onResume()

        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            updateCalender(transactions,(myViewModel.selectedMonth.value ?: 0),
                (myViewModel.selectedYear.value ?: 0) + 2010)
        }
    }

}