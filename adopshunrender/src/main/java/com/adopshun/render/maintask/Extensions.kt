package com.adopshun.render.maintask

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast

import androidx.appcompat.widget.AppCompatImageView
import com.adopshun.render.R

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


object Extensions {

    fun ImageView.setImage(uri: String, imageView: ImageView, width: String?, height: String?) {
        Glide.with(context).load(uri).placeholder(R.drawable.top_icon)
            .apply(RequestOptions.overrideOf(width!!.toInt(), height!!.toInt())).into(imageView)
    }




    fun spToPx(sp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            context.resources.displayMetrics
        ).toInt()
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


    fun isImageFile(filename: String): Boolean {
        val regex = Regex("(.*)(\\.gif)\$")
        return regex.matches(filename)
    }
    fun ImageView.loadGifImage( imageUri: String, imageView: AppCompatImageView, width: String,
                                height: String){
        Glide.with(context).asGif()
            .apply(RequestOptions().override(width.toInt(), height.toInt())).
        load(imageUri).into(imageView)
    }
    fun ImageView.loadImage(
        imageUri: String,
        imageView: AppCompatImageView,
        width: String,
        height: String
    ){

    Glide.with(context).load(imageUri)
            .apply(RequestOptions().override(width.toInt(), height.toInt()))
            .into(imageView)

    /*    val imageLoader = ImageLoader.Builder(this.context)
            .componentRegistry { add(SvgDecoder(this@loadImage.context)) }
            .build()

        load(uri = imageUri, imageLoader = imageLoader) {
            crossfade(false)
            size(width.toInt(), height.toInt())
            placeholder(R.drawable.placeholder)
        }*/
    }

    fun Context.dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    fun Context.pxToDp(px: Int): Int {
        return (px / resources.displayMetrics.density).toInt()
    }

    fun showToast(context: Context,msg:String){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
    fun showLog(msg:String){
        Log.d("Log---",msg)

    }


}