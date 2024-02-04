package com.example.tornado_ip_access

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*

class PanelActivity : AppCompatActivity() {

    lateinit var  program_spinner : Spinner
    lateinit var temp_spinner : Spinner
    lateinit var save_button : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panel)

        program_spinner = findViewById(R.id.program_cycle_spinner)
        temp_spinner  = findViewById(R.id.temp_spinner)

        var programs  = resources.getStringArray(R.array.program_options)
        var adapter = ArrayAdapter(this , android.R.layout.simple_spinner_item ,  programs)
        program_spinner.adapter = adapter

        program_spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                Toast.makeText(this@PanelActivity,
                    getString(R.string.selected_item) + " " +
                            "" + programs[position], Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
    }
    }

        var temps = resources.getStringArray(R.array.temp_options)
        var temp_adapter = ArrayAdapter(this ,android.R.layout.simple_spinner_item, temps)
        temp_spinner.adapter =temp_adapter


        temp_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*> , view: View, position: Int, id: Long) {
                Toast.makeText(this@PanelActivity,
                    getString(R.string.selected_item) + " " +
                            "" + programs[position], Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
        save_button = findViewById(R.id.save_settings)
        save_button.setOnClickListener {
            startActivity(Intent(this , Dashboard_Activity::class.java))
        }
    }
}