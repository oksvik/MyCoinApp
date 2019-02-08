package com.dudar.mycoinapp.views

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.dudar.mycoinapp.*
import com.dudar.mycoinapp.model.PriceData
import com.dudar.mycoinapp.viewmodels.GraphViewModel
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class GraphActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var graph: GraphView
    private lateinit var btnMonth: Button
    private lateinit var btnMonth3: Button
    private lateinit var btnMonth6: Button
    private lateinit var btnYear: Button
    private lateinit var toolbar: Toolbar
    private lateinit var txtTapedPoint : TextView

    private lateinit var graphViewModel: GraphViewModel
    private lateinit var curTimeSpan: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigation_drawer_graph)

        initViews()
        setSupportActionBar(toolbar)
        setupNavigationDrawer()
        setupButtons()

        graphViewModel = ViewModelProviders.of(this).get(GraphViewModel::class.java)

        curTimeSpan = savedInstanceState?.getString(SAVED_PERIOD_KEY) ?: PERIOD_MONTH
        graphViewModel.setTimespan(curTimeSpan)
        graphViewModel.pricesLiveData.observe(this, Observer { pricesList -> drawGraph(pricesList) })

        updateButtons()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawer_layout_graph)
        graph = findViewById(R.id.graph)
        txtTapedPoint = findViewById(R.id.txtTapedPoint)

        btnMonth = findViewById(R.id.btn_month)
        btnMonth3 = findViewById(R.id.btn_month3)
        btnMonth6 = findViewById(R.id.btn_month6)
        btnYear = findViewById(R.id.btn_year)
    }

    private fun setupNavigationDrawer() {
        val navigationView: NavigationView = findViewById(R.id.navigation_view_graph)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_list -> {
                    val intent = Intent(this, ListActivity::class.java)
                    startActivity(intent)
                }
            }
            drawerLayout.closeDrawers()
            true
        }
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun setupButtons() {
        btnMonth.setOnClickListener(this)
        btnMonth3.setOnClickListener(this)
        btnMonth6.setOnClickListener(this)
        btnYear.setOnClickListener(this)
    }

    private fun drawGraph(marketData: List<PriceData>?) {
        val points = mutableListOf<DataPoint>()
        marketData?.forEach { points.add(generatePoint(it)) }

        val series = LineGraphSeries<DataPoint>(points.toTypedArray())

        val sdf = SimpleDateFormat("dd.MMM", Locale.getDefault())
        graph.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(this, sdf)
        graph.gridLabelRenderer.numHorizontalLabels = 10 //because of the space

        // set manual x bounds to have nice steps
        val oneDay = SECONDS_PRO_DAY * NUMBER_OF_MILLISECONDS
        graph.viewport.setMinX(points[0].x - oneDay)
        graph.viewport.setMaxX(points[points.size - 1].x + oneDay)
        graph.viewport.isXAxisBoundsManual = true
        when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> graph.gridLabelRenderer.setHorizontalLabelsAngle(90)
            Configuration.ORIENTATION_LANDSCAPE -> graph.gridLabelRenderer.setHorizontalLabelsAngle(45)
        }

        series.setOnDataPointTapListener { _, dataPoint ->
            val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
            txtTapedPoint.text = getString(R.string.graph_taped_point_text, dataPoint.y.roundToInt(),dateFormat.format(dataPoint.x ))
        }

        series.isDrawBackground = true
        graph.addSeries(series)
    }

    private fun generatePoint(priceData: PriceData): DataPoint {
        val date = Date(priceData.timestamp * NUMBER_OF_MILLISECONDS)
        val curPrice = priceData.price

        return DataPoint(date, curPrice)
    }

    override fun onClick(view: View?) {
        var newTimeSpan=""
        when (view?.id) {
            R.id.btn_month -> {
                newTimeSpan = PERIOD_MONTH
            }
            R.id.btn_month3 -> {
                newTimeSpan = PERIOD_MONTH_3
            }
            R.id.btn_month6 -> {
                newTimeSpan = PERIOD_MONTH_6
            }
            R.id.btn_year -> {
                newTimeSpan = PERIOD_YEAR
            }
        }
        if (newTimeSpan != curTimeSpan){
            curTimeSpan = newTimeSpan
            txtTapedPoint.text=""
            updateButtons()
            graphViewModel.setTimespan(curTimeSpan)
        }
    }

    private fun updateButtons() {
        btnMonth.setBackgroundResource(0)
        btnMonth3.setBackgroundResource(0)
        btnMonth6.setBackgroundResource(0)
        btnYear.setBackgroundResource(0)

        when (curTimeSpan) {
            PERIOD_MONTH -> btnMonth.setBackgroundResource(R.drawable.buttonframe)
            PERIOD_MONTH_3 -> btnMonth3.setBackgroundResource(R.drawable.buttonframe)
            PERIOD_MONTH_6 -> btnMonth6.setBackgroundResource(R.drawable.buttonframe)
            PERIOD_YEAR -> btnYear.setBackgroundResource(R.drawable.buttonframe)
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(SAVED_PERIOD_KEY, curTimeSpan)
    }
}
