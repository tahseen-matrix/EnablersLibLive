package com.matrix.enablersliblive

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.adopshun.creator.maincreator.AdopshunCreator
import com.adopshun.render.maintask.RenderPopup
import com.adopshun.render.model.SegmentModel
import com.matrix.enablersliblive.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var mAdapter: RecyclerAdapter? = null
    private var mAdapter1: RecyclerAdapter? = null
    private val isStoreSegmentData: Boolean = false
    private val list = listOf(
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
    private var lastBackPressTime = 0L
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastBackPressTime < 2000) {
                // If the user has clicked back twice within 2 seconds, exit the app
                finish()
            } else {
                // Show a toast message to inform the user to click back again to exit
                Toast.makeText(this@MainActivity, "Press back again to exit", Toast.LENGTH_SHORT)
                    .show()
                // Update the lastBackPressTime variable
                lastBackPressTime = currentTime
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // Add the callback to the activity's OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, callback)

        mAdapter = RecyclerAdapter(list.toMutableList(), this)
        mAdapter1 = RecyclerAdapter(list1.toMutableList(), this)
        binding.rvImagae.adapter = mAdapter
        binding.rvImagae2.adapter = mAdapter1

        val viewGroup =
            (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
        AdopshunCreator.initAdopshun(this, viewGroup)
        AdopshunCreator.initLayout(R.layout.activity_main)

        RenderPopup.showPopups(this, R.layout.activity_main, userId = "45", 45)
        binding.navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    true
                }
                R.id.navigation_notifications -> {
                    true
                }
                else -> false
            }
        }

        //Store Segment Data
        if (isStoreSegmentData) {
            RenderPopup.storeSegmentData(
                context =
                this,
                segmentModel = SegmentModel(
                    segment_id = 46, unique_project_id = packageName.toString(), fields = mapOf(
                        "user_id" to "45",
                        "products" to "445",
                        "amount" to "4"
                    )
                ),
                authToken = "eyJpdiI6Ikp1STRLT1ZobDFnUmhtWTJYYVZqSlE9PSIsInZhbHVlIjoieEl5U3drRUN1TU9qdzBPaWNVaU16bmh1SG8yWi8vRityc1FPZ1owUWE2OWFOaTljSVJzR0NxaUp4Nmp4anpaRCIsIm1hYyI6IjA5MDc4ZTc3MTI4MDg3MjE2ZWYxNjhlYjUxOTA0NDVjNjZiNmExNzc4ZWQ1YTllMmE2MDk5YjMxMjNlZjEwY2UiLCJ0YWciOiIifQ=="
            )
        }

    }


    override fun onResume() {
        super.onResume()
        AdopshunCreator.initLayout(R.layout.activity_main)
    }
}