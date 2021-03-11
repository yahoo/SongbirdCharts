package com.yahoo.mobile.android.audiocharts.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.yahoo.mobile.android.audiocharts.R
import com.yahoo.mobile.android.songbird.model.ChartViewModel
import com.yahoo.mobile.android.songbird.view.SongbirdChartView
import com.yahoo.mobile.android.audiocharts.sample.settings.SettingsActivity
import com.yahoo.mobile.android.audiocharts.repository.AudioChartRepository

class SampleSongbirdChartActivity : AppCompatActivity() {

    companion object {
        const val X_AXIS_EXTRA = "X_AXIS_EXTRA"
        const val Y_AXIS_EXTRA = "Y_AXIS_EXTRA"
        @JvmStatic
        fun intent(context: Context, showXAxis: Boolean = true, showYAxis: Boolean = true) =
            Intent(context, SampleSongbirdChartActivity::class.java).apply {
                putExtra(X_AXIS_EXTRA, showXAxis)
                putExtra(Y_AXIS_EXTRA, showYAxis)
            }
    }

    private lateinit var chartViewModel: ChartViewModel
    private lateinit var songbirdChartView: SongbirdChartView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sample_songbird_chart)
        songbirdChartView = findViewById(R.id.songbird_view)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setOnClickListener {
            playSummaryAudio()
        }
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.summary_accessible_native_chart)
        }

        loadSampleSongbirdChart(
            showXAxis = intent.getBooleanExtra(X_AXIS_EXTRA, true),
            showYAxis = intent.getBooleanExtra(Y_AXIS_EXTRA, true)
        )
    }

    private fun loadSampleSongbirdChart(showXAxis: Boolean, showYAxis: Boolean) {
        setChartViewModel(
            // Your implementation of how the AudioChartViewModel is created
            AudioChartRepository.loadSampleSongbirdChart(
                showXAxis,
                showYAxis
            )
        )
    }

    private fun setChartViewModel(chartViewModel: ChartViewModel) {
        this.chartViewModel = chartViewModel
        songbirdChartView.setViewModel(chartViewModel)
    }

    private fun playSummaryAudio() {
        songbirdChartView.playSummaryAudio()
    }

    fun finalize() {
        songbirdChartView.dispose()
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_accessible_chart, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.settings -> {
                startActivity(
                    SettingsActivity.intent(this)
                )
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
