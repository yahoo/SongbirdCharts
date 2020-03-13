package com.yahoo.mobile.android.audiocharts.main

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import com.yahoo.mobile.android.audiocharts.R
import com.yahoo.mobile.android.audiocharts.sample.SampleSongbirdChartActivity

class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.apply {
            title = getString(R.string.songbird_chart)
        }
        findViewById<Button>(R.id.button).setOnClickListener {
            startActivity(
                SampleSongbirdChartActivity.intent(
                    this@MainActivity,
                    showXAxis = findViewById<CheckBox>(R.id.x_axis).isChecked,
                    showYAxis = findViewById<CheckBox>(R.id.y_axis).isChecked
                )
            )
        }
    }

}