package com.adopshun.creator.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import android.view.View
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

object ScreenshotUtils {
    /*  Method which will return Bitmap after taking screenshot. We have to pass the view which we want to take screenshot.  */
    @JvmStatic
    fun getScreenShot(view: View): Bitmap {
        val screenView = view
        screenView.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(screenView.drawingCache)
        screenView.isDrawingCacheEnabled = false
        return bitmap


    }

    /*  Create Directory where screenshot will save for sharing screenshot  */
    @JvmStatic
    fun getMainDirectoryName(context: Context): File {
        //Here we will use getExternalFilesDir and inside that we will make our Demo folder
        //benefit of getExternalFilesDir is that whenever the app uninstalls the images will get deleted automatically.
        val mainDir = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Demo"
        )

        //If File is not present create directory
        if (!mainDir.exists()) {
            if (mainDir.mkdir()) Log.e("Create Directory", "Main Directory Created : $mainDir")
        }
        return mainDir
    }

    /*  Store taken screenshot into above created path  */
    @JvmStatic
    fun store(bm: Bitmap, fileName: String?, saveFilePath: File): File {
        val dir = File(saveFilePath.absolutePath)
        if (!dir.exists()) dir.mkdirs()
        val file = File(saveFilePath.absolutePath, fileName)
        try {
            val fOut = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut)
            fOut.flush()
            fOut.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file
    }
}