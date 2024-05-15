package com.matrix.enablersliblive

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.adopshun.creator.maincreator.AdopshunCreator
import com.adopshun.render.maintask.RenderPopup
import com.adopshun.render.model.SegmentModel
import com.matrix.enablersliblive.databinding.ActivityColorBinding

class ColorActivity : AppCompatActivity() {
    private val binding: ActivityColorBinding by lazy {
        ActivityColorBinding.inflate(layoutInflater)
    }

    private val isStoreSegmentData: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            val fullText = "The quick brown fox jumps over the lazy dog"
            val substrings = arrayOf("quick", "fox")

            val builder = SpannableStringBuilder(fullText)

            for (substring in substrings) {
                val start = fullText.indexOf(substring)
                if (start >= 0) {
                    val end = start + substring.length
                    val colorSpan = ForegroundColorSpan(Color.RED)
                    builder.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }

            textTv.text = builder.toString()
        }

        // Enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val viewGroup =
            (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
        AdopshunCreator.initAdopshun(this, viewGroup)
        AdopshunCreator.initLayout(R.layout.activity_color)
        RenderPopup.showPopups(this, R.layout.activity_color, token = Constants.token)

        //Store Segment Data
        if (isStoreSegmentData) {
            RenderPopup.addSegmentData(
                context =
                this,
                segmentModel = SegmentModel(
                    segment_id = 48, unique_project_id = packageName.toString(), fields = mapOf(
                        "user_id" to "20",
                        "product_id" to "45",
                        "quantity" to "4"
                    )
                ),
                authToken = Constants.token
            )
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
        AdopshunCreator.initLayout(R.layout.activity_color)
    }
}