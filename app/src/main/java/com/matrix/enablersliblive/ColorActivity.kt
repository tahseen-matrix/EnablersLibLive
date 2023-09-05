package com.matrix.enablersliblive

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import com.adopshun.creator.maincreator.AdopshunCreator
import com.adopshun.render.maintask.RenderPopup
import com.matrix.enablersliblive.databinding.ActivityColorBinding
import com.matrix.enablersliblive.databinding.ActivityMainBinding

class ColorActivity : AppCompatActivity() {
    private val binding: ActivityColorBinding by lazy {
        ActivityColorBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            val stringTest =  "The quick brown fox {jumps over} the {lazy dog}"
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
            textTv.text = builder
        }

        val viewGroup = (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
        AdopshunCreator.initAdopshun(this,viewGroup)
        AdopshunCreator.initLayout(R.layout.activity_color)
        RenderPopup.showPopups(this, R.layout.activity_color)

    }

    override fun onResume() {
        super.onResume()
        AdopshunCreator.initLayout( R.layout.activity_color)
    }
}