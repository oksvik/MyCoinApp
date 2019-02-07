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
import android.util.Log
import android.view.View
import android.widget.Button
import com.dudar.mycoinapp.*
import com.dudar.mycoinapp.model.PriceData
import com.dudar.mycoinapp.viewmodels.GraphViewModel
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.text.SimpleDateFormat
import java.util.*


class GraphActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var graph: GraphView
    private lateinit var btnMonth: Button
    private lateinit var btnMonth3: Button
    private lateinit var btnMonth6: Button
    private lateinit var btnYear: Button
    private lateinit var toolbar: Toolbar

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

        btnMonth = findViewById(R.id.btn_month)
        btnMonth3 = findViewById(R.id.btn_month3)
        btnMonth6 = findViewById(R.id.btn_month6)
        btnYear = findViewById(R.id.btn_year)
    }

    private fun setupNavigationDrawer() {
        val navigationView: NavigationView = findViewById(R.id.navigation_view_graph)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            //menuItem.isChecked()=true
            when (menuItem.itemId) {
                com.dudar.mycoinapp.R.id.nav_list -> {
                    //open another activity
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
        //graph.addSeries(series)

        val sdf = SimpleDateFormat("dd.MMM", Locale.getDefault())
        graph.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(this, sdf)
        graph.gridLabelRenderer.numHorizontalLabels = 10 //because of the space
        Log.d("GraphActivity", " labels number=${graph.gridLabelRenderer.numHorizontalLabels}")

        // set manual x bounds to have nice steps
        graph.viewport.setMinX(points[0].x - 86400 * 1000)
        graph.viewport.setMaxX(points[points.size - 1].x + 86400 * 1000)

        Log.d("GraphActivity", "min x: at ${sdf.format(points[0].x - 86400 * 1000)} ")
        Log.d("GraphActivity", "max x: at ${sdf.format(points[points.size - 1].x + 86400 * 1000)} ")

        graph.viewport.isXAxisBoundsManual = true
        when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> graph.gridLabelRenderer.setHorizontalLabelsAngle(90)
            Configuration.ORIENTATION_LANDSCAPE -> graph.gridLabelRenderer.setHorizontalLabelsAngle(45)
        }

        /*
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
    @Override
    public void onTap(Series series, DataPointInterface dataPoint) {
        Toast.makeText(getActivity(), "Series1: On Data Point clicked: "+dataPoint, Toast.LENGTH_SHORT).show();
    }
});
         */

        graph.addSeries(series)
    }

    private fun generatePoint(priceData: PriceData): DataPoint {
        val date = Date(priceData.timestamp * NUMBER_OF_MILLISECONDS)
        val curPrice = priceData.price

        return DataPoint(date, curPrice)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_month -> {
                curTimeSpan = PERIOD_MONTH
            }

            R.id.btn_month3 -> {
                curTimeSpan = PERIOD_MONTH_3
            }
            R.id.btn_month6 -> {
                curTimeSpan = PERIOD_MONTH_6
            }
            R.id.btn_year -> {
                curTimeSpan = PERIOD_YEAR
            }
        }
        updateButtons()
        graphViewModel.setTimespan(curTimeSpan)

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
