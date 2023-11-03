package com.matrix.enablersliblive

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.adopshun.creator.maincreator.AdopshunCreator
import com.adopshun.render.maintask.RenderPopup
import com.adopshun.render.model.SegmentModel
import com.matrix.enablersliblive.databinding.ActivityColorBinding
import com.matrix.enablersliblive.databinding.ActivityMainBinding

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


        val viewGroup =
            (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
        AdopshunCreator.initAdopshun(this, viewGroup)
        AdopshunCreator.initLayout(R.layout.activity_color)
        RenderPopup.showPopups(this, R.layout.activity_color, "45",45)

        //Store Segment Data
        if (isStoreSegmentData) {
            RenderPopup.storeSegmentData(
                context =
                this,
                segmentModel = SegmentModel(
                    segment_id = 48, unique_project_id = packageName.toString(), fields = mapOf(
                        "user_id" to "20",
                        "product_id" to "45",
                        "quantity" to "4"
                    )
                ),
                authToken = "eyJpdiI6Ikp1STRLT1ZobDFnUmhtWTJYYVZqSlE9PSIsInZhbHVlIjoieEl5U3drRUN1TU9qdzBPaWNVaU16bmh1SG8yWi8vRityc1FPZ1owUWE2OWFOaTljSVJzR0NxaUp4Nmp4anpaRCIsIm1hYyI6IjA5MDc4ZTc3MTI4MDg3MjE2ZWYxNjhlYjUxOTA0NDVjNjZiNmExNzc4ZWQ1YTllMmE2MDk5YjMxMjNlZjEwY2UiLCJ0YWciOiIifQ=="
            )
        }

        }


    override fun onResume() {
        super.onResume()
        AdopshunCreator.initLayout(R.layout.activity_color)
    }
}