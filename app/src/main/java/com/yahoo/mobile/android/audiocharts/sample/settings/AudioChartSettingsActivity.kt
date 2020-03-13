package com.yahoo.mobile.android.audiocharts.sample.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.yahoo.mobile.android.songbird.util.AudioChartSettingsHelper
import com.yahoo.mobile.android.audiocharts.R
import com.yahoo.mobile.android.audiocharts.base.ApplicationBase

class AudioChartSettingsActivity : AppCompatActivity() {

    companion object {
        @JvmStatic
        fun intent(context: Context) = Intent(context, AudioChartSettingsActivity::class.java)
    }

    private val audioChartSettingsHelper: AudioChartSettingsHelper = AudioChartSettingsHelper(
        ApplicationBase.instance
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_accessible_chart_settings)

        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.settings)
        }
        val orderButtons: RadioGroup = findViewById(R.id.order_buttons)
        orderButtons.setOnCheckedChangeListener { _, checkedId ->
            audioChartSettingsHelper.setOrderPosition(orderButtons.indexOfChild(orderButtons.findViewById(checkedId)))
        }

        val dateButtons: RadioGroup = findViewById(R.id.date_buttons)
        dateButtons.setOnCheckedChangeListener { _, checkedId ->
            audioChartSettingsHelper.setDatePosition(dateButtons.indexOfChild(dateButtons.findViewById(checkedId)))
        }

        val maxFreqInput = findViewById<EditText>(R.id.max_freq_input)
        maxFreqInput.addTextChangedListener(
            object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.isNotEmpty() == true) {
                        audioChartSettingsHelper.setMaximumFrequency(s.toString().toInt())
                    }
                }
            }
        )

        val minFreqInput = findViewById<EditText>(R.id.min_freq_input)
        minFreqInput.addTextChangedListener(
            object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.isNotEmpty() == true) {
                        audioChartSettingsHelper.setMinimumFrequency(s.toString().toInt())
                    }
                }
            }
        )

        val echoButton = findViewById<ToggleButton>(R.id.echo_button)
        echoButton.setOnClickListener {
            audioChartSettingsHelper.switchEchoToggle()
        }

        (orderButtons.getChildAt(audioChartSettingsHelper.getSavedOrderPosition()) as RadioButton).isChecked = true
        (dateButtons.getChildAt(audioChartSettingsHelper.getSavedDateOrderPosition()) as RadioButton).isChecked = true
        maxFreqInput.setText(audioChartSettingsHelper.getMaximumFrequency().toString())
        minFreqInput.setText(audioChartSettingsHelper.getMinimumFrequency().toString())
        echoButton.isChecked = audioChartSettingsHelper.getEchoChecked()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
