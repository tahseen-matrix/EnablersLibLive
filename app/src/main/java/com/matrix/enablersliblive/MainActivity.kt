package com.matrix.enablersliblive

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.adopshun.creator.maincreator.AdopshunCreator
import com.adopshun.render.maintask.RenderPopup
import com.matrix.enablersliblive.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var mAdapter: RecyclerAdapter? = null
    private var mAdapter1: RecyclerAdapter? = null
    val list = listOf(
        R.drawable.img_plant_2,
        R.drawable.img_plant_9,
        R.drawable.img_plant_4,
        R.drawable.img_plant_9
    )
    private val list1 = listOf(
        R.drawable.img_plant_2,
        R.drawable.img_plant_9,
        R.drawable.img_plant_4,
        R.drawable.img_plant_9,
        R.drawable.img_plant_4,
        R.drawable.img_plant_4,
        R.drawable.img_plant_9,
        R.drawable.img_plant_4,
        R.drawable.img_plant_4,
        R.drawable.img_plant_9,
        R.drawable.img_plant_4
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mAdapter = RecyclerAdapter(list.toMutableList(), this)
        mAdapter1 = RecyclerAdapter(list1.toMutableList(), this)
        binding.rvImagae.adapter = mAdapter
        binding.rvImagae2.adapter = mAdapter1

        val viewGroup = (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
        AdopshunCreator.initAdopshun(this,viewGroup)
        AdopshunCreator.initLayout(R.layout.activity_main)

        RenderPopup.showPopups(this, R.layout.activity_main)
        binding.navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    Toast.makeText(this@MainActivity, "Home", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_notifications -> {
                    Toast.makeText(this@MainActivity, "Notification", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        AdopshunCreator.initLayout(R.layout.activity_main)
    }
}