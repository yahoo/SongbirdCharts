package com.yahoo.mobile.android.audiocharts.sample.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.yahoo.mobile.android.songbird.util.SettingsHelper
import com.yahoo.mobile.android.audiocharts.R
import com.yahoo.mobile.android.audiocharts.base.ApplicationBase
import com.yahoo.mobile.android.songbird.player.AudioPlayer

class SettingsActivity : AppCompatActivity() {

    companion object {
        @JvmStatic
        fun intent(context: Context) = Intent(context, SettingsActivity::class.java)
    }

    private val settingsHelper: SettingsHelper = SettingsHelper(
        ApplicationBase.instance
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.settings)
        }
        val orderButtons: RadioGroup = findViewById(R.id.order_buttons)
        orderButtons.setOnCheckedChangeListener { _, checkedId ->
            settingsHelper.setOrderPosition(orderButtons.indexOfChild(orderButtons.findViewById(checkedId)))
        }

        val dateButtons: RadioGroup = findViewById(R.id.date_buttons)
        dateButtons.setOnCheckedChangeListener { _, checkedId ->
            settingsHelper.setDatePosition(dateButtons.indexOfChild(dateButtons.findViewById(checkedId)))
        }

        val highToneInput = findViewById<Spinner>(R.id.high_tone_input)
        ArrayAdapter.createFromResource(
            this,
            R.array.high_tones,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            highToneInput.adapter = adapter
            highToneInput.setSelection(AudioPlayer.tones.size-1-settingsHelper.getMaximumTone())
        }
        highToneInput.onItemSelectedListener =
            object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    settingsHelper.setMaximumTone(AudioPlayer.tones.size-1-position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    settingsHelper.setMaximumTone(AudioPlayer.tones.size-1)
                }
            }

        val lowToneInput = findViewById<Spinner>(R.id.low_tone_input)
        ArrayAdapter.createFromResource(
            this,
            R.array.low_tones,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            lowToneInput.adapter = adapter
            lowToneInput.setSelection(settingsHelper.getMinimumTone())
        }
        lowToneInput.onItemSelectedListener =
            object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    settingsHelper.setMinimumTone(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    settingsHelper.setMinimumTone(0)
                }
            }

        val echoButton = findViewById<ToggleButton>(R.id.echo_button)
        echoButton.setOnClickListener {
            settingsHelper.switchEchoToggle()
        }

        val feedbackButton = findViewById<Button>(R.id.feedback)
        feedbackButton.setOnClickListener {
            Toast.makeText(this, getString(R.string.visit_us_at_repo), Toast.LENGTH_LONG).show()
        }

        (orderButtons.getChildAt(settingsHelper.getSavedOrderPosition()) as RadioButton).isChecked = true
        (dateButtons.getChildAt(settingsHelper.getSavedDateOrderPosition()) as RadioButton).isChecked = true
        echoButton.isChecked = settingsHelper.getEchoChecked()
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
