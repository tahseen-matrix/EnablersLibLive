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
import com.matrix.enablersliblive.databinding.ActivityCartBinding

class CartActivity : AppCompatActivity() {
    private val binding: ActivityCartBinding by lazy {
        ActivityCartBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.apply {
            val cartItems = listOf(
                CartItem("Item 1", 10.0, 2),
                CartItem("Item 2", 15.0, 1),
                // Add more items as needed
            )

            val cartAdapter = CartAdapter(this@CartActivity, cartItems)
            cartListView.adapter = cartAdapter

            checkoutButton.setOnClickListener {
                // Add your checkout logic here
            }
        }

        val viewGroup =
            (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
        AdopshunCreator.initAdopshun(this, viewGroup)
        AdopshunCreator.initLayout(R.layout.activity_cart)

       // Toast.makeText(this, "${MainActivity.userId}", Toast.LENGTH_SHORT).show()
        when (MainActivity.userId) {
            401-> {
                RenderPopup.addSegmentData(
                    context =
                    this,
                    segmentModel = SegmentModel(
                        segment_id = 62, unique_project_id = packageName.toString(), fields = mapOf(
                            "user_id" to MainActivity.userId.toString()
                        )
                    ),
                    authToken = Constants.token
                )
                RenderPopup.showPopups(
                    this,
                    R.layout.activity_cart,
                    userId = MainActivity.userId.toString(),
                    token = Constants.token
                )
            }
            else -> {
                RenderPopup.showPopups(this, R.layout.activity_cart, token = Constants.token)
            }
        }
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

    override fun onResume() {
        super.onResume()
        AdopshunCreator.initLayout(R.layout.activity_cart)
    }
}