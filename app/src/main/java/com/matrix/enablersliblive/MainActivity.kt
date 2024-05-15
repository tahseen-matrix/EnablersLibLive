package com.matrix.enablersliblive

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.adopshun.creator.maincreator.AdopshunCreator
import com.adopshun.render.maintask.RenderPopup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.matrix.enablersliblive.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var mAdapter1: RecyclerAdapter? = null
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
    companion object{
        var selectedIdList = ""
        var userId:Int = -1
    }
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

        supportActionBar?.title = selectedIdList
        // Add the callback to the activity's OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, callback)

        mAdapter1 = RecyclerAdapter(list1.toMutableList(), this)
        binding.rvImagae2.adapter = mAdapter1

        val viewGroup =
            (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
        AdopshunCreator.initAdopshun(this, viewGroup)
        AdopshunCreator.initLayout(R.layout.activity_main)

        when(userId){
            301, 302->{
                RenderPopup.showPopups(this, R.layout.activity_main, userId= userId.toString(), token = Constants.token)
            }
            else->{
                RenderPopup.showPopups(this, R.layout.activity_main, token = Constants.token)
            }
        }

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


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.cart_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cart -> {
                // Handle cart item click
                startActivity(Intent(this@MainActivity, CartActivity::class.java))
                true
            }
            R.id.action_logout -> {
                showAlertDialog(this@MainActivity){
                    // Start the login activity
                    selectedIdList = ""
                    userId = -1
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()  // Close the current activity
                }

                true
            }
            // Add other menu items handling if needed
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAlertDialog(
        context: Context,
        onPositiveClick: () -> Unit
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setCancelable(false)
            .setNegativeButton("Cancel"){_,_->}
            .setPositiveButton("Logout") { _, _ ->
                // Call the provided positive button response
                onPositiveClick()
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        AdopshunCreator.initLayout(R.layout.activity_main)
    }

    override fun onDestroy() {
        super.onDestroy()
        selectedIdList = ""
        userId = -1
    }
}