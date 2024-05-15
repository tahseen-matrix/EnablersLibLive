package com.matrix.enablersliblive

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.adopshun.creator.maincreator.AdopshunCreator
import com.adopshun.render.maintask.RenderPopup
import com.adopshun.render.model.SegmentModel
import com.matrix.enablersliblive.databinding.ActivityDeatilBinding

class DeatilActivity : AppCompatActivity() {
    private val binding: ActivityDeatilBinding by lazy {
        ActivityDeatilBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deatil)

        // Enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.apply {
            // Set restaurant details to views
            restaurantDetailImageView.setImageResource(R.drawable.img_plant_2) // You can load images using a library like Picasso or Glide
        }
      //  Toast.makeText(this, "${MainActivity.userId}", Toast.LENGTH_SHORT).show()
        val viewGroup =
            (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
        AdopshunCreator.initAdopshun(this, viewGroup)
        AdopshunCreator.initLayout(R.layout.activity_deatil)

        when (MainActivity.userId) {
             402 -> {
                RenderPopup.addSegmentData(
                    context =
                    this,
                    segmentModel = SegmentModel(
                        segment_id = 44, unique_project_id = packageName.toString(), fields = mapOf(
                            "user_id" to MainActivity.userId.toString()
                        )
                    ),
                    authToken = Constants.token
                )
                RenderPopup.showPopups(
                    this,
                    R.layout.activity_deatil,
                    userId = MainActivity.userId.toString(),
                    token = Constants.token
                )
            }
            else -> {
                RenderPopup.showPopups(this, R.layout.activity_deatil, token = Constants.token)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        AdopshunCreator.initLayout(R.layout.activity_deatil)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Handle the Up button click
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}