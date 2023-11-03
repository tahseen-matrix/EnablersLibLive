package com.adopshun.creator.utils

import android.content.Context
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.json.JSONObject
import org.json.JSONTokener
import kotlin.math.roundToInt


object Extensions {


    fun dpFromPx(context: Context, px: Float): Float {
        return px / context.resources.displayMetrics.density
    }

    fun pxFromDp(context: Context, dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }


    fun Context.dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    fun Context.pxToDp(px: Int): Int {
        return (px / resources.displayMetrics.density).toInt()
    }




     fun Context.getDPsFromPixels(pixels: Int): Int {
        val resources = resources
        return (pixels / (resources.displayMetrics.densityDpi / 160f)).roundToInt()
    }
     fun Context.getActualDPsFromPixels( pixels: Int): Float {
        val resources = resources
        return pixels / (resources.displayMetrics.densityDpi / 160f)
    }




    fun View.centerX():Int{
        val myViewRect = Rect()
        val cy: Float = myViewRect.exactCenterX()

        return cy.toInt()
    }
    fun View.centerY():Int{
        val myViewRect = Rect()
        val cy: Float = myViewRect.exactCenterY()
        return cy.toInt()
    }

    fun Context.convertInoPx(value: Int): Float {
        val scale: Float = resources.displayMetrics.density
        return (value * scale + 0.5f)
    }



    fun View.getAllChildrenViews(): ArrayList<View> {
        val result = ArrayList<View>()
        if (this !is ViewGroup) {
            result.add(this)
        } else {
            for (index in 0 until this.childCount) {
                val child = this.getChildAt(index)
                result.addAll(child.getAllChildrenViews())
            }
        }
        return result
    }




    fun isJSONValid(jsonString: String): Boolean {
        return try {
            JSONObject(JSONTokener(jsonString))
            // If parsing succeeds, it's valid JSON
            true
        } catch (e: Exception) {
            // If parsing fails, it's not valid JSON
            false
        }
    }
    fun AppCompatActivity.toast(message:String){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    fun log(message: String){
        Log.d("TAG",message)
    }

    fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }
        })
    }

    fun EditText.onTextChanged(onTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onTextChanged.invoke(s.toString())

            }

            override fun afterTextChanged(editable: Editable?) {
            }
        })
    }


    fun Fragment.toast(message:String){
        Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
    }


    fun View.setMarginTop(marginTop: Int) {
        val menuLayoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
        menuLayoutParams.setMargins(0, marginTop, 0, 0)
        this.layoutParams = menuLayoutParams
    }

}