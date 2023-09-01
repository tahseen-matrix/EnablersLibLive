package com.adopshun.creator.utils

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.text.*
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.adopshun.creator.R
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
    fun Context.doToPixels(dp: Int): Int {
        val px = dp * (160 / 160)
        return px
    }



     fun Context.getDPsFromPixels(pixels: Int): Int {
        val resources = resources
        return (pixels / (resources.displayMetrics.densityDpi / 160f)).roundToInt()
    }
     fun Context.getActualDPsFromPixels( pixels: Int): Float {
        val resources = resources
        return pixels / (resources.displayMetrics.densityDpi / 160f)
    }


    private fun getScreenResolution(context: Context): String? {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display = wm.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        return "{$width,$height}"
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

    fun Context.convertInoPx(value:Int): Float {
        val scale: Float = getResources().getDisplayMetrics().density
        val pixels = (value * scale + 0.5f)

        return pixels
    }

    fun View.absX(): Int
    {
        val location = IntArray(2)
        this.getLocationInWindow(location)
        return location[0]
    }



    fun View.absY(): Int
    {
        val location = IntArray(2)
        this.getLocationInWindow(location)
        return location[1]
    }

    fun AppCompatActivity.replaceFragmentWithBundle(fragment: Fragment, frameId: Int, bundle:Bundle,addToStack: Boolean) {

        fragment.arguments = bundle
        supportFragmentManager.inTransaction {
            if (addToStack) replace(frameId, fragment, fragment.javaClass.simpleName)
                .addToBackStack(fragment.javaClass.simpleName)
            else
                replace(frameId, fragment, fragment.javaClass.simpleName)
        }

    }


    inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().commit()
    }



    fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int) {
        supportFragmentManager.inTransaction { add(frameId, fragment, fragment.javaClass.simpleName) }
    }

    fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int) {
        supportFragmentManager.inTransaction { replace(frameId, fragment, fragment.javaClass.simpleName) }
    }

    fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int, addToStack: Boolean) {
        supportFragmentManager.inTransaction {
            if (addToStack) replace(frameId, fragment, fragment.javaClass.simpleName)
                .addToBackStack(fragment.javaClass.simpleName)
            else
                replace(frameId, fragment, fragment.javaClass.simpleName)
        }
    }

    fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int, addToStack: Boolean, clearBackStack: Boolean) {
        supportFragmentManager.inTransaction {

            if (clearBackStack && supportFragmentManager.backStackEntryCount > 0) {
                val first = supportFragmentManager.getBackStackEntryAt(0)
                supportFragmentManager.popBackStack(first.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }

            if (addToStack) replace(frameId, fragment, fragment.javaClass.simpleName)
                .addToBackStack(fragment.javaClass.simpleName)
            else
                replace(frameId, fragment, fragment.javaClass.simpleName)
        }
    }

    fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int, addToStack: Boolean) {
        supportFragmentManager.inTransaction {
            if (addToStack) add(frameId, fragment, fragment.javaClass.simpleName)
                .addToBackStack(fragment.javaClass.simpleName)
            else add(frameId, fragment)
        }
    }


    fun AppCompatActivity.getCurrentFragment(): Fragment? {
        val fragmentManager = supportFragmentManager
        var fragmentTag: String? = ""

        if (fragmentManager.backStackEntryCount > 0)
            fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.backStackEntryCount - 1).name

        return fragmentManager.findFragmentByTag(fragmentTag)
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


    fun AppCompatActivity.transparentStatusBar(){
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            statusBarColor = ContextCompat.getColor(context, R.color.transparent)
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
    fun AppCompatActivity.fromHtml(html: String?): Spanned? {
        return if (html == null) {
            // return an empty spannable if the html is null
            SpannableString("")
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // FROM_HTML_MODE_LEGACY is the behaviour that was used for versions below android N
            // we are using this flag to give a consistent behaviour
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(html)
        }
    }





}