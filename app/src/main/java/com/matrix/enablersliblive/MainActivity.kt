package com.matrix.enablersliblive

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
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
        mAdapter = RecyclerAdapter(list.toMutableList())
        mAdapter1 = RecyclerAdapter(list1.toMutableList())
        binding.rvImagae.adapter = mAdapter
        binding.rvImagae2.adapter = mAdapter1

        val viewGroup = (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
        AdopshunCreator.initAdopshun(this,viewGroup)
        AdopshunCreator.initLayout(R.layout.activity_main)
        runOnUiThread {
            RenderPopup.showPopups(this,  R.layout.activity_main)
        }
    }

    override fun onResume() {
        super.onResume()
        AdopshunCreator.initLayout(R.layout.activity_main)
    }
}