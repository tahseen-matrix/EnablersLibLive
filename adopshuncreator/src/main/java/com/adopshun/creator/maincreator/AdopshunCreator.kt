package com.adopshun.creator.maincreator

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.adopshun.creator.R
import com.adopshun.creator.models.CustomModel
import com.adopshun.creator.models.JsonModel
import com.adopshun.creator.models.QRModel
import com.adopshun.creator.qrcode.QRResult
import com.adopshun.creator.qrcode.ScanQRCode
import com.adopshun.creator.retrofit.RetrofitService
import com.adopshun.creator.utils.Extensions
import com.adopshun.creator.utils.Extensions.getAllChildrenViews
import com.adopshun.creator.utils.Extensions.isJSONValid
import com.adopshun.creator.utils.Extensions.pxToDp
import com.adopshun.creator.utils.ScreenshotUtils
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*


object AdopshunCreator : CustomModel.OnCustomStateListener {

    @JvmStatic
    private lateinit var floatingActionButton: MovableFloatingActionButton
    // var mLayout :Int? = null
    // var mContext :AppCompatActivity? = null

    @JvmStatic
    var mLayout = 0
    @JvmStatic
    fun initLayout(layout: Int){
        mLayout = layout

    }
    @JvmStatic
    fun initAdopshun(
        context: AppCompatActivity,
        viewGroup: ViewGroup
    ) {

        floatingActionButton = setupFloatingActionButton(context, viewGroup)

        val scanQrCode = context.registerForActivityResult(ScanQRCode()) { result ->
            // handle QRResult
            when (result) {
                is QRResult.QRSuccess -> {
                    val value = result.content.rawValue
                    if (isJSONValid(value)){
                        val jObject = JSONObject(value)
                        val userId = jObject.getString("user_id")
                        val uniqueId = jObject.getString("unique_id")
                        val sessionId = jObject.getString("session_id")
                        val projectName = jObject.getString("project_name")

                        floatingActionButton.visibility = View.GONE
                        val width: Int = context.pxToDp(viewGroup.width)
                        val height: Int = context.pxToDp(viewGroup.height)


                        val hashMap = HashMap<String, Any>()
                        hashMap["Height"] = height
                        hashMap["Width"] = width
                        hashMap["UserId"] = userId!!
                        hashMap["UniqueId"] = uniqueId!!
                        hashMap["SessionId"] = sessionId!!
                        hashMap["ScreenId"] = mLayout
                        hashMap["ProjectName"] = projectName
                        hashMap["UniqueProjectId"] = context.applicationContext.packageName

                        sendAllViews(context, viewGroup, hashMap)
                    }
                    floatingActionButton.visibility = View.VISIBLE


                }
                QRResult.QRUserCanceled -> "User canceled"
                QRResult.QRMissingPermission -> {
                    context.startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        )
                    )
                }
                is QRResult.QRError -> "${result.exception.javaClass.simpleName}: ${result.exception.localizedMessage}"

            }

        }

        floatingActionButton.setOnClickListener {
            scanQrCode.launch(null)

        }
    }

    @JvmStatic
    private fun sendAllViews(
        context: AppCompatActivity,
        layout: ViewGroup,
        hashMap: HashMap<String, Any>) {
        val height = hashMap["Height"]
        val width = hashMap["Width"]
        val userId = hashMap["UserId"]
        val uniqueId = hashMap["UniqueId"]
        val sessionId = hashMap["SessionId"]
        val screenId = hashMap["ScreenId"]
        val projectName = hashMap["ProjectName"]
        val uniqueProjectId = hashMap["UniqueProjectId"]

        val mainJsonObject = JsonModel()
        var counter = 100

        val viewsList = layout.getAllChildrenViews()

        for (i in 0 until viewsList.size) {
            // viewsList[i].id = ViewIdGenerator.generateViewId()
            val view = viewsList[i]
            val controllerObject = JsonModel().Controller()

            val rectf = Rect()
            //For coordinates location relative to the parent
            view.getLocalVisibleRect(rectf)
            //For coordinates location relative to the screen/display
            view.getGlobalVisibleRect(rectf)

            val offsetViewBounds = Rect()
            //returns the visible bounds
            view.getDrawingRect(offsetViewBounds)
            // calculates the relative coordinates to the parent
            layout.offsetDescendantRectToMyCoords(view, offsetViewBounds)

            val relativeLeft = offsetViewBounds.left
            val relativeTop = offsetViewBounds.top

            controllerObject.id = counter.toString()
            controllerObject.width = context.pxToDp(view.width).toString()
            controllerObject.height = context.pxToDp(view.height).toString()
            controllerObject.pointX = context.pxToDp(relativeLeft).toString()
            controllerObject.pointY = context.pxToDp(relativeTop).toString()

            if (view is AppCompatTextView) {
                controllerObject.title = view.text.toString()
            }
            if (view is TextView) {
                controllerObject.title = view.text.toString()
            }

            Extensions.log("JSONObjects----> $controllerObject")
            counter++

            mainJsonObject.metaData.controllers.add(controllerObject)

        }


        mainJsonObject.metaData.screenSizeHeight = height.toString()
        mainJsonObject.metaData.screenSizeWidth = width.toString()
        mainJsonObject.metaData.screen_id = screenId.toString()

        val gson = Gson()
        val jsonString = gson.toJson(mainJsonObject)
        val json = gson.fromJson(jsonString, JsonModel::class.java)

        Extensions.log("JSON-------->" + json.metaData.controllers.toString())

        val bit = ScreenshotUtils.getScreenShot(layout)

        callApi(
            bit,
            jsonString,
            context,
            userId.toString(),
            uniqueId.toString(),
            sessionId.toString(),
            projectName.toString(),
            uniqueProjectId.toString(),
            screenId.toString())

    }


    @JvmStatic
    private fun callApi(
        bitmap: Bitmap,
        jsonString: String,
        context: Context,
        user_id: String,
        unique_id: String,
        session_id: String,
        project_name: String,
        uniqueProject_id: String,
        screenId: String
    ) {
        val mediaType = "text/plain"
        val service = RetrofitService.getInstance(context)
        val userId = user_id.toRequestBody(mediaType.toMediaTypeOrNull())
        val uniqueId = unique_id.toRequestBody(mediaType.toMediaTypeOrNull())
        val metaData = jsonString.toRequestBody(mediaType.toMediaTypeOrNull())
        val sessionId = session_id.toRequestBody(mediaType.toMediaTypeOrNull())
        val projectName = project_name.toRequestBody(mediaType.toMediaTypeOrNull())
        val screeId = screenId.toRequestBody(mediaType.toMediaTypeOrNull())
        val uniqueProjectId = uniqueProject_id.toRequestBody(mediaType.toMediaTypeOrNull())
        val file = buildImageBodyPart("image", bitmap, context)

        service.sendScreenshot(
            userId,
            uniqueId,
            metaData,
            sessionId,
            projectName,
            screeId,
            uniqueProjectId,
            file
        ).enqueue(object :
            Callback<QRModel> {

            override fun onResponse(call: Call<QRModel>, response: Response<QRModel>?) {
                // llProgressBar.visibility = View.GONE
                when (response?.code()) {
                    200 -> {
                        Extensions.log(response.message())
                        CustomModel.instance?.apiCall(response.body()!!.message!!)
                    }
                    404 -> {
                        Extensions.log("Not found")
                        CustomModel.instance?.apiCall(response.body()!!.message!!)
                    }
                    500 -> {
                        Extensions.log("Internal Server Error")
                        CustomModel.instance?.apiCall(response.body()!!.message!!)
                    }
                    else -> {
                        val errorMessage =
                            response?.code().toString() + "The unique project id has already been taken."
                        Extensions.log(errorMessage)
                        CustomModel.instance?.apiCall("The unique project id has already been taken.")
                    }
                }

            }

            override fun onFailure(call: Call<QRModel>, t: Throwable) {
                Extensions.log(t.toString())
                //llProgressBar.visibility = View.GONE
                CustomModel.instance?.apiCall(t.toString())

            }
        })
    }






    @JvmStatic
    private fun convertBitmapToFile(fileName: String, bitmap: Bitmap, context: Context): File {
        //create a file to write bitmap data
        val file = File(context.cacheDir, fileName)
        file.createNewFile()

        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos)
        val bitMapData = bos.toByteArray()

        //write the bytes in file
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        try {
            fos?.write(bitMapData)
            fos?.flush()
            fos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    @JvmStatic
    private fun buildImageBodyPart(
        fileName: String,
        bitmap: Bitmap,
        context: Context
    ): MultipartBody.Part {
        val leftImageFile = convertBitmapToFile(fileName, bitmap, context)
        val reqFile = leftImageFile.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(fileName, leftImageFile.name, reqFile)
    }



    @JvmStatic
    private fun setupFloatingActionButton(
        context: AppCompatActivity,
        viewGroup: ViewGroup
    ): MovableFloatingActionButton {


        val inflater = LayoutInflater.from(context)
        floatingActionButton = inflater.inflate(
            R.layout.moveable_button,
            viewGroup, false) as MovableFloatingActionButton
        val cord = setActivityRoot(context)

        cord.addView(floatingActionButton)
        (floatingActionButton.layoutParams as CoordinatorLayout.LayoutParams).gravity = Gravity.BOTTOM or Gravity.END

        return floatingActionButton

    }
    @JvmStatic
    private fun setActivityRoot(c: Activity): CoordinatorLayout {
        val v = (c.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)
        val sv = CoordinatorLayout(c)
        val lp = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        sv.layoutParams = lp
        // With this line
        sv.setBackgroundColor(ContextCompat.getColor(c, android.R.color.white))
        (v.parent as ViewGroup).removeAllViews()
        sv.addView(v as View)
        c.addContentView(sv, lp)
        return sv
    }

    override fun onApiCall(message: String?) {


    }


}