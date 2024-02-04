package com.example.tornado_ip_access

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import java.math.RoundingMode
import java.sql.Time
import java.util.*
import kotlin.collections.ArrayList

class Dashboard_Activity : AppCompatActivity() {

    private var listView: ListView? = null
    private var itemsAdapters : ItemAdapter_main? = null
    private var arrayList: ArrayList<ItemList_main>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        var listItem : ArrayList<ItemList_main> = ArrayList()

        listView = findViewById(R.id._listview_main)
        arrayList = ArrayList()
        arrayList = listItem
        itemsAdapters = ItemAdapter_main(applicationContext , arrayList!!)
        listView?.adapter = itemsAdapters

        listItem.add(ItemList_main("Product" , "Wash Machine" ,"Model", "1","Device","12", Date().toString()))
        listItem.add(ItemList_main("Product" , "Air conditioner" ,"Model", "13","Device","12", Date().toString()))
        listItem.add(ItemList_main("Product" , "Refrigator" ,"Model", "13","Device","12", Date().toString()))
        listItem.add(ItemList_main("Product" , "Heater" ,"Model", "13","Device","12", Date().toString()))
        listItem.add(ItemList_main("Product" , "TV" ,"Model", "13","Device","12", Date().toString()))

        // setting up click listeners

        listView?.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, ItemDisplayActivity::class.java)
            intent.putExtra("product", listItem[id.toInt()].detail_g)
            intent.putExtra("model",listItem[id.toInt()].detail_t)
            intent.putExtra("device" , listItem[id.toInt()].detail_h)

            startActivity(intent)
        }
    }
}