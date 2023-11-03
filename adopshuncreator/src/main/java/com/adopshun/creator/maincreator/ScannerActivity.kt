package com.adopshun.creator.maincreator

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.adopshun.creator.R
import com.adopshun.creator.models.CustomModel
import com.adopshun.creator.models.JsonModel
import com.adopshun.creator.retrofit.RetrofitService
import com.adopshun.creator.utils.Extensions.getAllChildrenViews
import com.adopshun.creator.utils.Extensions.log
import com.adopshun.creator.utils.Extensions.pxToDp
import com.adopshun.creator.utils.RuntimePermissionUtil
import com.adopshun.creator.utils.RuntimePermissionUtil.requestPermission
import com.adopshun.creator.utils.ScreenshotUtils
import com.google.gson.Gson
import com.adopshun.creator.qrcode.QRResult
import com.adopshun.creator.qrcode.ScanQRCode
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.*


class ScannerActivity : BaseActivity() {

    private val cameraPerm = Manifest.permission.CAMERA

    //private lateinit var codeScanner: CodeScanner
    var hasCameraPermission = false
    var PERMISSION_REQUEST_CODE = 100

    var viewsList: ArrayList<View> = ArrayList()
    private lateinit var mainJsonObject: JsonModel

    var user_id: String? = null
    var unique_id: String? = null
    var session_id: String? = null
    var project_name: String? = null
    var counter = 100
    private lateinit var floatingActionButton: MovableFloatingActionButton
    private var scanQrCode = registerForActivityResult(ScanQRCode(), ::showSnackbar)
    //private lateinit var binding: ActivityScannerBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // binding = ActivityScannerBinding.inflate(layoutInflater)
        //setContentView(binding.root)
        //  setContentView(R.layout.activity_scanner)
        hasCameraPermission = RuntimePermissionUtil.checkPermissonGranted(this, cameraPerm)

        if (!hasCameraPermission) {
            requestPermission(this, cameraPerm, PERMISSION_REQUEST_CODE)
        }

        qrSetup()

        /*btnScanAgain.setOnClickListener {
            codeScanner.startPreview()
            btnScanAgain.visibility = View.INVISIBLE
        }*/

    }


    private fun showSnackbar(result: QRResult) {
        val text = when (result) {
            is QRResult.QRSuccess -> {
                val value = result.content.rawValue
                //  toast(value)
                // llProgressBar.visibility = View.VISIBLE
                val jObject = JSONObject(value)
                user_id = jObject.getString("user_id")
                unique_id = jObject.getString("unique_id")
                session_id = jObject.getString("session_id")
                project_name = jObject.getString("project_name")

                log(user_id!!)
                log(unique_id!!)
                log(session_id!!)

                finish()
                CustomModel.instance!!.onQrScan(user_id, unique_id!!, session_id!!, project_name!!)
            }
            QRResult.QRUserCanceled -> "User canceled"
            QRResult.QRMissingPermission -> "Missing permission"
            is QRResult.QRError -> "${result.exception.javaClass.simpleName}: ${result.exception.localizedMessage}"
        }

        /*    Snackbar.make(binding.root, text, Snackbar.LENGTH_INDEFINITE).apply {
                view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)?.run {
                    maxLines = 5
                    setTextIsSelectable(true)
                }
                if (result is QRResult.QRSuccess && result.content is QRContent.Url) {
                    setAction("Open") { openUrl(result.content.rawValue) }
                } else {
                    setAction("Ok") { }
                }
            }.show()*/
    }

    private fun openUrl(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (ignored: ActivityNotFoundException) {
            // no Activity found to run the given Intent
        }
    }


    private fun qrSetup() {
        scanQrCode = registerForActivityResult(ScanQRCode(), ::showSnackbar)
        scanQrCode.launch(null)
        /*  codeScanner = CodeScanner(this, scanner_view)

          codeScanner.decodeCallback = DecodeCallback {

              this.runOnUiThread {
                  Log.d("QReader", "Value : $it")
                  shakeItBaby(this)
                  try {

                      llProgressBar.visibility = View.VISIBLE
                      val jObject = JSONObject(it.toString())
                      user_id = jObject.getString("user_id")
                      unique_id = jObject.getString("unique_id")
                      session_id = jObject.getString("session_id")
                      project_name = jObject.getString("project_name")

                      log(user_id!!)
                      log(unique_id!!)
                      log(session_id!!)
                      sessionManager!!.setString(AppConstants.USER_ID, user_id)
                      sessionManager!!.setString(AppConstants.UNIQUE_ID, unique_id)
                      sessionManager!!.setString(AppConstants.SESSION_ID, session_id)

                      finish()
                      CustomModel.instance!!.onQrScan(user_id, unique_id!!, session_id!!,project_name!!)
                  } catch (e: Exception) {
                      log(e.toString())
                      toast("Invalid QR")
                      llProgressBar.visibility = View.GONE
                      btnScanAgain.visibility = View.VISIBLE
                  }
              }
          }
          codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
              runOnUiThread {
                  log("Camera initialization error: ${it.message}")
              }
          }*/

    }

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

    private fun buildImageBodyPart(
        fileName: String,
        bitmap: Bitmap,
        context: Context
    ): MultipartBody.Part {
        val leftImageFile = convertBitmapToFile(fileName, bitmap, context)
        val reqFile = RequestBody.create("image/*".toMediaTypeOrNull(), leftImageFile)
        return MultipartBody.Part.createFormData(fileName, leftImageFile.name, reqFile)
    }

    private fun callApi(
        bitmap: Bitmap,
        jsonString: String,
        context: Context,
        user_id: String,
        unique_id: String,
        session_id: String,
        projectName: String,
        uniqueProject_id: String
    ) {
        val service = RetrofitService.getInstance(context)
        val userId = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            user_id
        )

        val uniqueId = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            unique_id
        )

        val metaData = RequestBody.create("text/plain".toMediaTypeOrNull(), jsonString)

        val sessionId = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            session_id
        )
        val projectName = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            projectName
        )
        val uniqueProjectId = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            uniqueProject_id
        )

        val file = buildImageBodyPart("image", bitmap, context)

     /*   service.sendScreenshot(
            userId,
            uniqueId,
            metaData,
            sessionId,
            projectName,
            uniqueProjectId,

            file
        ).enqueue(object :
            Callback<QRModel> {

            override fun onResponse(call: Call<QRModel>, response: Response<QRModel>) {
                // llProgressBar.visibility = View.GONE

                if (response.code() == 200) {
                    log(response.message())
                    CustomModel.instance!!.apiCall(response.body()!!.message!!)

                } else if (response.code() == 404) {
                    log("Not found")
                    CustomModel.instance!!.apiCall(response.body()!!.message!!)

                } else if (response.code() == 500) {
                    log("Internal Server Error")
                    CustomModel.instance!!.apiCall(response.body()!!.message!!)

                } else {
                    log(
                        response.code().toString() + "The unique project id has already been taken."
                    )
                    //  CustomModel.instance!!.apiCall("The unique project id has already been taken.")
                }

            }

            override fun onFailure(call: Call<QRModel>, t: Throwable) {
                log(t.toString())
                //llProgressBar.visibility = View.GONE
                CustomModel.instance!!.apiCall(t.toString())

            }
        })*/
    }


    fun setupFloatingActionButton(
        context: AppCompatActivity,
        viewGroup: ViewGroup
    ): MovableFloatingActionButton {
        val inflater = LayoutInflater.from(context)
        floatingActionButton = inflater.inflate(
            R.layout.moveable_button,
            viewGroup,
            false
        ) as MovableFloatingActionButton


        var rootgroup: ViewGroup
        if (viewGroup is ScrollView) {
            val nextChild = (viewGroup as ViewGroup).getChildAt(0)
            if (nextChild is ConstraintLayout) {
                rootgroup = nextChild
                rootgroup.addView(floatingActionButton)

            }
            if (nextChild is LinearLayout) {
                rootgroup = nextChild
                rootgroup.addView(floatingActionButton, 0)

            }
            if (nextChild is FrameLayout) {
                rootgroup = nextChild
                rootgroup.addView(floatingActionButton, 0)


            }
            if (nextChild is RelativeLayout) {
                rootgroup = nextChild
                rootgroup.addView(floatingActionButton, 0)

            }

        } else {
            rootgroup = viewGroup
            rootgroup.addView(floatingActionButton, 0)

        }
        floatingActionButton.setOnClickListener {
            qrSetup()
        }

        return floatingActionButton


    }


    private fun openQRReader(context: AppCompatActivity) {

        // rootScanner.visibility = View.VISIBLE
        // qrSetup()

        val scannerActivity = "com.adopshun.creator.maincreator.ScannerActivity"
        try {
            val scannerClass = Class.forName(scannerActivity)
            val intent = Intent(context, scannerClass)
            context.startActivity(intent)
        } catch (e: ClassNotFoundException) {
            Log.e("SplashScrActivity", e.message!!)
        }
    }

    override fun onPause() {
        //codeScanner.releaseResources()
        super.onPause()
    }


    override fun onResume() {
        super.onResume()
        //codeScanner.startPreview()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //  Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_SHORT).show()
                // main logic
            } else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT)
                    .show()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        showMessageOKCancel(
                            "You need to allow access permissions"
                        ) { dialog, which ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermission(this, cameraPerm, PERMISSION_REQUEST_CODE)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showMessageOKCancel(
        message: String,
        okListener: DialogInterface.OnClickListener
    ) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    fun sendAllViews(
        context: AppCompatActivity,
        layout: ViewGroup,
        view: MovableFloatingActionButton,
        hashMap: HashMap<String, Any>
    ) {
        val height = hashMap["Height"]
        val width = hashMap["Width"]
        val userId = hashMap["UserId"]
        val uniqueId = hashMap["UniqueId"]
        val sessionId = hashMap["SessionId"]
        val screenId = hashMap["ScreenId"]
        val projectName = hashMap["ProjectName"]
        val uniqueProjectId = hashMap["UniqueProjectId"]

        mainJsonObject = JsonModel()

        viewsList = layout.getAllChildrenViews()
        for (i in 0 until viewsList.size) {
            // viewsList[i].id = ViewIdGenerator.generateViewId()
            val view = viewsList[i]
            log(view.toString())
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


            viewsList[i].id = counter
            controllerObject.id = viewsList[i].id.toString()
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

            log("JSONObjects----> $controllerObject")
            counter++

            mainJsonObject.metaData.controllers.add(controllerObject)

        }


        mainJsonObject.metaData.screenSizeHeight = height.toString()
        mainJsonObject.metaData.screenSizeWidth = width.toString()
        mainJsonObject.metaData.screen_id = screenId.toString()

        val gson = Gson()
        val jsonString = gson.toJson(mainJsonObject)
        val json = gson.fromJson(jsonString, JsonModel::class.java)

        log("JSON-------->" + json.metaData.controllers.toString())

        val bit = ScreenshotUtils.getScreenShot(layout)
        view.visibility = View.VISIBLE

        callApi(
            bit,
            jsonString,
            context,
            userId.toString(),
            uniqueId.toString(),
            sessionId.toString(),
            projectName.toString(),
            uniqueProjectId.toString()
        )
    }


}


/*
            when (view) {

                is BottomNavigationView -> {
                    controllerObject.id = ViewIdGenerator.generateViewId().toString()
                    controllerObject.width = context.pxToDp(view.width).toString()
                    controllerObject.height = context.pxToDp(view.height).toString()
                    controllerObject.pointX = context.pxToDp(view.left).toString()
                    controllerObject.pointY = context.pxToDp(view.top).toString()
                    Log.d("JSONObject", controllerObject.toString())
                }


                is RecyclerView -> {

                    controllerObject.id = ViewIdGenerator.generateViewId().toString()
                    controllerObject.width =
                        context.pxToDp(view.findViewHolderForAdapterPosition(0)!!.itemView.width)
                            .toString()
                    controllerObject.height =
                        context.pxToDp(view.findViewHolderForAdapterPosition(0)!!.itemView.height)
                            .toString()
                    controllerObject.pointX =
                        context.pxToDp(view.findViewHolderForAdapterPosition(0)!!.itemView.absX())
                            .toString()
                    controllerObject.pointY =
                        context.pxToDp(view.findViewHolderForAdapterPosition(0)!!.itemView.absY())
                            .toString()
                    Log.d("JSONObject", controllerObject.toString())
                }

                else -> {

                    val rectf = Rect()
                    //For coordinates location relative to the parent
                    view.getLocalVisibleRect(rectf)
                    //For coordinates location relative to the screen/display
                    view.getGlobalVisibleRect(rectf)

                    Log.d("WIDTH        :", java.lang.String.valueOf(rectf.width()))
                    Log.d("HEIGHT       :", java.lang.String.valueOf(rectf.height()))
                    Log.d("left         :", java.lang.String.valueOf(rectf.left))
                    Log.d("right        :", java.lang.String.valueOf(rectf.right))
                    Log.d("top          :", java.lang.String.valueOf(rectf.top))
                    Log.d("bottom       :", java.lang.String.valueOf(rectf.bottom))

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

                    if (view is AppCompatTextView){
                        controllerObject.title = view.text.toString()
                    }
                    if(view is TextView){
                        controllerObject.title = view.text.toString()
                    }

                    Log.d("JSONObject", controllerObject.toString())
                    counter++
                }
            }
*/

//  /*  private fun connectSocket() {
//        try {
//            mSocket = IO.socket(AppConstants.BASE_URL)
//        } catch (e: URISyntaxException) {
//            throw RuntimeException(e)
//        }
//
//        //Register all the listener and callbacks here.
//        mSocket!!.on(Socket.EVENT_CONNECT, onConnect)
//        mSocket!!.on(Socket.EVENT_DISCONNECT, onDisconnect)
//        mSocket!!.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
//        mSocket!!.on(AppConstants.CAPTURED,onCaptured)
//        mSocket!!.connect()
//
//    }*/

/*
private fun takeScreenshot(): String {

    val bitmap = Screenshot.with(this).getScreenshot()
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)

    val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
    val baseString: String = Base64.encodeToString(byteArray, Base64.DEFAULT)
    return baseString

}

private fun takeScreenshotView(rootLayout: ScrollView): String {

    val v1 = rootLayout
    v1.isDrawingCacheEnabled = true
    val bitmap = Bitmap.createBitmap(v1.drawingCache)
    v1.isDrawingCacheEnabled = false

    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)

    val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
    val encoded: String = Base64.encodeToString(byteArray, Base64.DEFAULT)

    return encoded

}

*/


/*
fun setUpView(layout: Int) {
    // inflate your main layout here (use RelativeLayout or whatever your root ViewGroup type is
    mainLayout = this.layoutInflater.inflate(layout, null) as ScrollView

    // set a global layout listener which will be called when the layout pass is completed and the view is drawn
    mainLayout!!.viewTreeObserver.addOnGlobalLayoutListener(
        object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                //Remove the listener before proceeding
                mainLayout!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
                mWidth = mainLayout!!.width
                mHeight = mainLayout!!.height
                Log.d("Height", mHeight.toString())
                Log.d("Width", mWidth.toString())
            }
        }
    )
}
*/


//
//    // <----- Callback functions ------->
//
///*     var onConnect = Emitter.Listener {
//        //After getting a Socket.EVENT_CONNECT which indicate socket has been connected to server,
//        //send userName and roomName so that they can join the room.
//      //  mSocket!!.emit("subscribe", jsonData)
//        log("Socket Connected")
//         //Join Room
//         mSocket!!.emit(AppConstants.EVENT_JOIN, AppConstants.ROOM_)
//
//        runOnUiThread {
//            if (!isConnected){
//                isConnected = true
//            }
//        }
//    }*/
//
///*     var onCaptured = Emitter.Listener {
//        //After getting a Socket.EVENT_CONNECT which indicate socket has been connected to server,
//        //send userName and roomName so that they can join the room.
//        //  mSocket!!.emit("subscribe", jsonData)
//        log("OnCapture")
//        runOnUiThread {
//            toast("data")
//            log("Captured------")
//        }
//    }
//
//    private val onDisconnect = Emitter.Listener {
//        runOnUiThread(Runnable {
//            Log.i(TAG, "diconnected")
//            isConnected = false
//
//        })
//    }
//
//    private val onConnectError = Emitter.Listener {
//        runOnUiThread(Runnable {
//            Log.e(TAG, "Error connecting")
//
//        })
//    }*/
//
//    override fun onDestroy() {
//        super.onDestroy()
//        //mSocket!!.emit("unsubscribe", jsonData)
////        mSocket!!.disconnect()
////        mSocket!!.off(Socket.EVENT_CONNECT, onConnect)
////        mSocket!!.off(Socket.EVENT_DISCONNECT, onDisconnect);
////        mSocket!!.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
//    }
/*  for (i in 0 until childCount) {

          when (val rootView = layout.getChildAt(i)) {
              is ScrollView -> {
                  val childViewsCount: Int = rootView.childCount
                  for (j in 0 until childViewsCount) {
                      val innerViews = rootView.getChildAt(j)
                      if (innerViews is MaterialTextView) {
                          viewsList.add(innerViews)
                      }
                      if (innerViews is Button) {
                          viewsList.add(innerViews)
                      }

                  }
              }

              is RelativeLayout -> {
                  val childViewsCount: Int = rootView.childCount
                  for (j in 0 until childViewsCount) {
                      val innerViews = rootView.getChildAt(j)
                      if (innerViews is RelativeLayout) {
                          viewsList.add(innerViews)
                      }
                      if (innerViews is LinearLayout) {
                          viewsList.add(innerViews)
                      }
                      if (innerViews is Button) {
                          viewsList.add(innerViews)
                      }
                  }
              }
              is LinearLayout -> {
                  val childViewsCount: Int = rootView.childCount
                  for (j in 0 until childViewsCount) {
                      val innerViews = rootView.getChildAt(j)
                      if (innerViews is MaterialTextView) {
                          viewsList.add(innerViews)
                      }
                      if (innerViews is Button) {
                          viewsList.add(innerViews)
                      }
                      if (innerViews is LinearLayout) {
                          val innerChildViewsCount: Int = innerViews.childCount
                          for (k in 0 until innerChildViewsCount) {
                              if (innerViews is MaterialTextView) {
                                  viewsList.add(innerViews)
                              }
                              if (innerViews is Button) {
                                  viewsList.add(innerViews)
                              }
                              if (innerViews is ImageView) {
                                  viewsList.add(innerViews)
                              }
                              if (innerViews is ImageButton) {
                                  viewsList.add(innerViews)
                              }

                              if (innerViews is CheckBox) {
                                  viewsList.add(innerViews)
                              }
                              if (innerViews is ToggleButton) {
                                  viewsList.add(innerViews)
                              }
                              if (innerViews is Switch) {
                                  viewsList.add(innerViews)
                              }


                          }

                          viewsList.add(innerViews)

                      }

                  }


              }

              is TableLayout -> {
                  val childViewsCount: Int = rootView.childCount
                  for (j in 0 until childViewsCount) {
                      val innerViews = rootView.getChildAt(j)
                      if (innerViews is MaterialTextView) {
                          viewsList.add(innerViews)
                      }

                  }
              }
              is FrameLayout -> {
                  val childViewsCount: Int = rootView.childCount
                  for (j in 0 until childViewsCount) {
                      val innerViews = rootView.getChildAt(j)
                      if (innerViews is MaterialTextView) {
                          viewsList.add(innerViews)
                      }

                  }
              }

              is TableRow -> {
                  val childViewsCount: Int = rootView.childCount
                  for (j in 0 until childViewsCount) {
                      val innerViews = rootView.getChildAt(j)
                      if (innerViews is MaterialTextView) {
                          viewsList.add(innerViews)
                      }

                  }
              }

              is Space -> {

              }

              is ConstraintLayout -> {
                  val childViewsCount: Int = rootView.childCount

                  for (k in 0 until childViewsCount) {
                      when (val innerViews = rootView.getChildAt(k)) {
                          is Toolbar -> {
                              viewsList.add(innerViews)
                              val innerToolbarCounts: Int = innerViews.childCount
                              for (l in 0 until innerToolbarCounts) {
                                  val innerToolbarViews = innerViews.getChildAt(l)

                                  if (innerToolbarViews is MaterialTextView) {
                                      viewsList.add(innerToolbarViews)
                                  }
                              }

                          }
                          is MaterialTextView -> {
                              viewsList.add(innerViews)
                          }
                          is RecyclerView -> {
                              viewsList.add(innerViews)

                          }
                          is BottomNavigationView -> {
                              viewsList.add(innerViews)

                          }
                          else -> {
                              viewsList.add(innerViews)

                          }
                      }

                  }
              }


              is Toolbar -> {
                  val childToolbarCount: Int = rootView.childCount
                  for (j in 0 until childToolbarCount) {
                      val toolbarViews = rootView.getChildAt(j)
                      if (toolbarViews is MaterialTextView) {
                          viewsList.add(toolbarViews)
                      }
                  }
              }
              is TextView -> {
                  viewsList.add(rootView)

              }
              is RecyclerView -> {
                  viewsList.add(rootView)

              }
              is BottomNavigationView -> {
                  viewsList.add(rootView)

              }
              else -> {
                  viewsList.add(rootView)

              }
          }
      }

     */
