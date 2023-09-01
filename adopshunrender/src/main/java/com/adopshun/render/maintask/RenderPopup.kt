package com.adopshun.render.maintask

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.InsetDrawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import com.adopshun.render.R
import com.adopshun.render.androidSimpleTooltip.OverlayView
import com.adopshun.render.androidSimpleTooltip.SimpleTooltip
import com.adopshun.render.database.AppDatabase
import com.adopshun.render.database.Step
import com.adopshun.render.databinding.PingLayoutBinding
import com.adopshun.render.databinding.PopupLayoutBinding
import com.adopshun.render.maintask.Extensions.dpToPx
import com.adopshun.render.maintask.Extensions.getAllChildrenViews
import com.adopshun.render.maintask.Extensions.isImageFile
import com.adopshun.render.maintask.Extensions.loadGifImage
import com.adopshun.render.maintask.Extensions.loadImage
import com.adopshun.render.maintask.Extensions.pxToDp
import com.adopshun.render.maintask.Extensions.showLog
import com.adopshun.render.maintask.Extensions.showToast
import com.adopshun.render.model.ApiInterfaceModel
import com.adopshun.render.model.RenderModel
import com.adopshun.render.retrofit.RetrofitService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Matcher
import java.util.regex.Pattern

object RenderPopup : ApiInterfaceModel.OnApiResponseListener {

    var alertDialog: AlertDialog? = null
    var index = 0
    var mLayout = 0
    var mTextId=0
    var mContext: AppCompatActivity? = null
    var popArray: ArrayList<RenderModel.IdentifierDesign> = ArrayList()
    var sessionManager: PreferencesManager? = null
    private lateinit var appDb: AppDatabase

    @SuppressLint("InvalidAnalyticsName")
    fun checkFirstRun(context: AppCompatActivity, layout: Int, textViewId:Int) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Do you want to show onboarding?")
        builder.setTitle("Adopshun")
        builder.setCancelable(false)
        builder.setPositiveButton("Yes") { _, _ ->
            context.getSharedPreferences("renderFirstPop", MODE_PRIVATE)?.edit()
                ?.putBoolean(AppConstants.IS_POP_STATUS, true)?.apply()
            context.getSharedPreferences("renderFirstPop", MODE_PRIVATE)?.edit()
                ?.putBoolean(AppConstants.IS_FIRST_RUN, true)?.apply()
            mLayout = layout
            mContext = context
            mTextId = textViewId
            appDb = AppDatabase.getDatabase(context)
            GlobalScope.launch(Dispatchers.IO) {
                appDb.stepDao().clearStep()
            }

            Handler(Looper.myLooper()!!).postDelayed({
                index = 0
                ApiInterfaceModel.instance!!.setListener(this)
                getPops(context, context.packageName)
            }, 500)


        }
        builder.setNegativeButton("No") { dialog, which ->
            context.getSharedPreferences("renderFirstPop", MODE_PRIVATE)?.edit()
                ?.putBoolean(AppConstants.IS_POP_STATUS, false)?.apply()
            context.getSharedPreferences("renderFirstPop", MODE_PRIVATE)?.edit()
                ?.putBoolean(AppConstants.IS_FIRST_RUN, true)?.apply()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }





    fun showPopups(context: AppCompatActivity, layout: Int, textViewId:Int) {
        PreferencesManager.initializeInstance(context = context)
        sessionManager = PreferencesManager.instance
        context.startService(Intent(context, OnClearFromRecentService::class.java))
        if (context.getSharedPreferences("renderFirstPop", MODE_PRIVATE)
                ?.getBoolean(AppConstants.IS_FIRST_RUN, false) == false
        ) {
            checkFirstRun(context, layout, textViewId)
        }
        if (context.getSharedPreferences("renderFirstPop", MODE_PRIVATE)
                ?.getBoolean(AppConstants.IS_POP_STATUS, false) == true
        ) {
            mLayout = layout
            mContext = context
            mTextId = textViewId
            appDb = AppDatabase.getDatabase(context)
            Handler(Looper.myLooper()!!).postDelayed({
                index = 0
                ApiInterfaceModel.instance!!.setListener(this)
                getPops(context, context.packageName)
            }, 500)
        }

    }

    private fun getPops(context: AppCompatActivity, applicationId: String) {

        showLog(applicationId)

        val service = RetrofitService.getInstance(context)
        service.getJson(applicationId).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val json = response.body()
                    ApiInterfaceModel.instance!!.apiCall(json)

                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                showToast(context, t.toString())
            }
        })
    }

    private fun popUp(
        context: AppCompatActivity,
        identifierDesign: ArrayList<RenderModel.IdentifierDesign>
    ) {

        sessionManager?.setBoolean(AppConstants.IS_POPPED_UP, true)


        popArray = identifierDesign

        when (popArray[index].dialogType) {
            AppConstants.DIALOG_TYPE.popup -> {
                createPopup(context, popArray[index])
            }
            AppConstants.DIALOG_TYPE.bottom -> {
                createPopup(context, popArray[index])
            }
            else -> {
                pingToolTip(context, popArray[index])
            }
        }
    }

    private fun getRootViewGroupPopUp(context: AppCompatActivity): ViewGroup {
        return (context.findViewById<View>(android.R.id.content) ) as ViewGroup
    }
    @SuppressLint("Range")
    private fun createPopup(
        context: AppCompatActivity,
        identifierDesign: RenderModel.IdentifierDesign
    ) {

        val mViewGroup = getRootViewGroupPopUp(context)

        val dialogView = PopupLayoutBinding.inflate(LayoutInflater.from(context))


        val outerLayout = identifierDesign.outerLayout
        val belongId = outerLayout.belongId

        val viewsList = mViewGroup.getAllChildrenViews()

        val rootRelative = dialogView.relativeRoot
        val rootLinear = dialogView.linearRoot
        val closeButton = dialogView.btnCancel
        val alertDialog = createAlertDialog(dialogView, context, outerLayout)
        val skipButton = dialogView.btnSkip


        val gravity = when (outerLayout.position) {
            "center" -> {
                Gravity.CENTER
            }
            "left" -> {
                Gravity.START
            }
            "right" -> {
                Gravity.END
            }
            else -> {
                Gravity.CENTER_HORIZONTAL
            }
        }

        rootRelative.gravity = gravity


        var counter2 = 100
        val arrayList = ArrayList<Int>()
        for (i in 0 until viewsList.size) {
            viewsList[i].id = counter2
            arrayList.add(viewsList[i].id)
            counter2++
        }


        var anchorView = View(context)
        for (i in 0 until viewsList.size) {
            if (viewsList[i].id.toString() == belongId) {
                anchorView = viewsList[i]
            }
        }

        val answerLayoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )

        val rectf = Rect()
        //For coordinates location relative to the parent
        anchorView.getLocalVisibleRect(rectf)
        //For coordinates location relative to the screen/display
        anchorView.getGlobalVisibleRect(rectf)

        val offsetViewBounds = Rect()
        //returns the visible bounds
        anchorView.getDrawingRect(offsetViewBounds)
        // calculates the relative coordinates to the parent
        mViewGroup.offsetDescendantRectToMyCoords(anchorView, offsetViewBounds)

        val relativeTop = offsetViewBounds.top

        val centerPoint = context.pxToDp(mViewGroup.height / 2)
        val viewY = context.pxToDp(relativeTop)

        if (viewY < centerPoint) {
            answerLayoutParams.addRule(RelativeLayout.BELOW, rootLinear.id)
            answerLayoutParams.setMargins(0, 50, 0, 50)
            skipButton.layoutParams = answerLayoutParams

        } else {
            answerLayoutParams.addRule(RelativeLayout.BELOW, skipButton.id)
            answerLayoutParams.setMargins(0, 50, 0, 0)
            rootLinear.layoutParams = answerLayoutParams
        }

        closeButton.setOnClickListener {
            dialogView.root.visibility = View.GONE
            alertDialog.dismiss()
            if (index < popArray.size - 1) {
                index++

                when (popArray[index].dialogType) {
                    AppConstants.DIALOG_TYPE.popup -> {
                        createPopup(context, popArray[index])
                    }
                    AppConstants.DIALOG_TYPE.bottom -> {
                        createPopup(context, popArray[index])
                    }
                    else -> {
                        pingToolTip(context, popArray[index])
                    }
                }
            }

        }

        val innerLayoutArray = identifierDesign.innerLayout

        for (j in innerLayoutArray.indices) {

            when (innerLayoutArray[j].type) {

                AppConstants.VIEWTYPE.IMAGE -> {

                    val image = AppCompatImageView(context)
                    image.id = View.generateViewId()

                    val height =
                        context.dpToPx(innerLayoutArray[j].height.toInt() + 15).toString()
                    val width = context.dpToPx(innerLayoutArray[j].width.toInt() + 15).toString()

                    innerLayoutArray[j].imageUrl.let {
                        it.let { it1 ->
                            if (isImageFile(it1)) {
                                image.loadGifImage(it1, image, width, height)
                            } else image.loadImage(it1, image, width, height)
                        }
                    }


                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )


                    if (innerLayoutArray[j].bottomMargin !== null) {
                        params.setMargins(
                            20,
                            -50,
                            20,
                            10
                        )
                    }

                    if (innerLayoutArray[j].topMargin !== null) {
                        params.setMargins(
                            20,
                            -50,
                            20,
                            10
                        )
                    }

                    params.gravity = Gravity.CENTER
                    image.layoutParams = params

                    rootLinear.addView(image)

                }

                AppConstants.VIEWTYPE.LABEL -> {

                    val title = AppCompatTextView(context)
                    title.id = View.generateViewId()
                    title.text = innerLayoutArray[j].title
                    title.textSize = innerLayoutArray[j].fontSize!!.toFloat()

                    applyFonts(context, title, innerLayoutArray[j])

                    title.setTextColor(Color.parseColor(innerLayoutArray[j].textColor))

                    val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )

                    rootLinear.addView(title)

                    title.setPadding(
                        10,
                        10,
                        10,
                        10
                    )


                    if (innerLayoutArray[j].fontSize.toInt() < 6) {
                        title.width = 450
                        title.gravity = Gravity.CENTER
                    }

                    when (innerLayoutArray[j].position) {

                        "center" -> {
                            title.gravity = Gravity.CENTER
                            params.gravity = Gravity.CENTER

                        }
                        "left" -> {
                            title.gravity = Gravity.START
                            params.gravity = Gravity.START

                        }
                        "right" -> {
                            title.gravity = Gravity.END
                            params.gravity = Gravity.END

                        }
                    }

                    title.layoutParams = params

                }

                AppConstants.VIEWTYPE.BUTTON -> {

                    val button = AppCompatButton(context)
                    button.id = View.generateViewId()
                    button.isAllCaps = false
                    button.text = innerLayoutArray[j].title
                    button.textSize = innerLayoutArray[j].fontSize!!.toFloat()

                    button.typeface = ResourcesCompat.getFont(context, R.font.opensans_regular)
                    button.setTextColor(Color.parseColor(innerLayoutArray[j].textColor))

                    applyFonts(context, button, innerLayoutArray[j])

                    ViewCompat.setBackgroundTintList(
                        button,
                        ColorStateList.valueOf(Color.parseColor(innerLayoutArray[j].backgroundColor))
                    )

                    val radius = 70 //radius will be 5px
                    val gradientDrawable = GradientDrawable()
                    gradientDrawable.setColor(context.resources.getColor(R.color.orange))
                    gradientDrawable.cornerRadius = radius.toFloat()
                    button.background = gradientDrawable

                    rootLinear.addView(button)

                    button.minHeight = 0
                    button.minimumHeight = 0

                    button.height = context.dpToPx(innerLayoutArray[j].height.toInt() + 7)

                    button.setPadding(25, 10, 25, 10)
                    val params: LinearLayout.LayoutParams
                    if (innerLayoutArray[j].width == "initial") {
                        params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )

                    } else {

                        params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )

                    }
                    when (innerLayoutArray[j].position) {

                        "center" -> {
                            params.gravity = Gravity.CENTER
                            button.gravity = Gravity.CENTER
                        }
                        "left" -> {
                            params.gravity = Gravity.START
                            button.gravity = Gravity.CENTER_VERTICAL
                        }
                        "end" -> {
                            params.gravity = Gravity.END
                            button.gravity = Gravity.CENTER_VERTICAL
                        }
                    }
                    button.layoutParams = params
                    button.textAlignment = View.TEXT_ALIGNMENT_CENTER

                    params.setMargins(
                        30, context.dpToPx(innerLayoutArray[j].topMargin.toInt() + 10), 30,
                        context.dpToPx(innerLayoutArray[j].bottomMargin.toInt() + 10)
                    )

                    button.layoutParams = params

                    button.setOnClickListener {
                        /* val bundle = Bundle()
                         bundle.putString(FirebaseAnalytics.Param.METHOD, "POP UP CLICK")
                         firebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, bundle)*/
                        dialogView.root.visibility = View.GONE
                        alertDialog.dismiss()

                        if (index < popArray.size - 1) {
                            index++
                            when (popArray[index].dialogType) {
                                AppConstants.DIALOG_TYPE.popup -> {
                                    createPopup(context, popArray[index])
                                }
                                AppConstants.DIALOG_TYPE.bottom -> {
                                    createPopup(context, popArray[index])
                                }
                                else -> {
                                    pingToolTip(context, popArray[index])
                                }
                            }
                        }
                        if (innerLayoutArray[j].buttonUrl.isNotEmpty()) {
                            Handler(Looper.myLooper()!!).postDelayed({
                                val URL_REGEX =
                                    "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$"
                                val p: Pattern = Pattern.compile(URL_REGEX)
                                val m: Matcher = p.matcher(innerLayoutArray[j].buttonUrl)
                                if (m.find()) {
                                    val browserIntent =
                                        Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(innerLayoutArray[j].buttonUrl)
                                        )
                                    context.startActivity(browserIntent)
                                }
                            }, 500)
                        }


                    }

                }
            }
        }

        if (!(context as Activity).isFinishing) {
            //show dialog
            alertDialog.show()
        }


        skipButton.setOnClickListener {
            alertDialog.dismiss()

        }

    }

    private fun getRootViewGroup(context: AppCompatActivity): ViewGroup {
        return (context.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
    }


    @SuppressLint("Range")
    internal fun pingToolTip(
        context: AppCompatActivity,
        identifierDesign: RenderModel.IdentifierDesign
    ) {

        GlobalScope.launch(Dispatchers.Main) {
            val binding = PingLayoutBinding.inflate(LayoutInflater.from(context))
            val mViewGroup = getRootViewGroup(context)
            val outerLayout = identifierDesign.outerLayout
            val belongId = outerLayout.belongId

            val viewsList = mViewGroup.getAllChildrenViews()

            val rootLinear = binding.linearRoot
            val dynamicLayout = binding.dynamicLayout
            val closeButton = binding.btnCancel
            val skipButton = binding.btnSkip

            var counter2 = 100
            val arrayList = ArrayList<Int>()
            for (i in 0 until viewsList.size) {
                viewsList[i].id = counter2
                arrayList.add(viewsList[i].id)
                counter2++
            }

            var anchorView = View(context)
            for (i in 0 until viewsList.size) {
                if (viewsList[i].id.toString() == belongId) {
                    anchorView = viewsList[i]
                }
            }

            val rootParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )

            val rectf = Rect()
            anchorView.getLocalVisibleRect(rectf)
            anchorView.getGlobalVisibleRect(rectf)

            val offsetViewBounds = Rect()
            anchorView.getDrawingRect(offsetViewBounds)
            mViewGroup.offsetDescendantRectToMyCoords(anchorView, offsetViewBounds)

            val relativeTop = offsetViewBounds.top

            val gravity: Int //Tooltip gravity
            val centerPoint = context.pxToDp(mViewGroup.height / 2)
            val viewY = context.pxToDp(relativeTop)
            val params: LinearLayout.LayoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            params.setMargins(50, 10, 50, 10)

            if (viewY < centerPoint) {
                gravity = Gravity.BOTTOM  //Tooltip gravity
                rootParams.addRule(RelativeLayout.BELOW, rootLinear.id)
                rootParams.setMargins(50, 50, 50, 50)
                skipButton.layoutParams = rootParams

            } else {
                gravity = Gravity.TOP  //Tooltip gravity
                rootParams.addRule(RelativeLayout.BELOW, skipButton.id)
                rootParams.setMargins(50, 50, 50, 0)
                rootLinear.layoutParams = rootParams
            }


            // Rest of your code here
            /*-------------Tooltip Start------------------------- */

            val tooltip = withContext(Dispatchers.Default){
                SimpleTooltip.Builder(context)
                    .anchorView(anchorView)
                    .gravity(gravity)
                    .dismissOnOutsideTouch(false)
                    .dismissOnInsideTouch(false)
                    .modal(true)
                    .setWidth(mViewGroup.width)
                    .transparentOverlay(false)
                    .highlightShape(OverlayView.HIGHLIGHT_SHAPE_RECTANGULAR)
                    .cornerRadius(20f)
                    .overlayOffset(5f)
                    .contentView(binding.root, mTextId)
                    .focusable(true)
                    .build()
            }
            tooltip.show()

            /*-------------Tooltip END------------------------- */
            skipButton.setOnClickListener {
                binding.root.visibility = View.GONE
                if (tooltip.isShowing) {
                    tooltip.dismiss()
                }
            }

            closeButton.setOnClickListener {
                tooltip.dismiss()
                if (index < popArray.size - 1) {
                    index++
                    when (popArray[index].dialogType) {
                        AppConstants.DIALOG_TYPE.popup -> {
                            createPopup(context, popArray[index])
                        }
                        AppConstants.DIALOG_TYPE.bottom -> {
                            createPopup(context, popArray[index])
                        }
                        else -> {
                            pingToolTip(context, popArray[index])
                        }
                    }
                }
            }

            val innerLayoutArray = identifierDesign.innerLayout

            for (j in innerLayoutArray.indices) {

                when (innerLayoutArray[j].type) {

                    AppConstants.VIEWTYPE.LABEL -> {

                        val title = AppCompatTextView(context)
                        title.id = View.generateViewId()
                        title.text = innerLayoutArray[j].title

                        title.setTextSize(
                            TypedValue.COMPLEX_UNIT_SP,
                            innerLayoutArray[j].fontSize!!.toFloat()
                        )
                        applyFonts(context, title, innerLayoutArray[j])

                        title.setTextColor(Color.parseColor(innerLayoutArray[j].textColor))

                        val paramsText = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )


                        dynamicLayout.addView(title)

                        if (innerLayoutArray[j].bottomMargin !== null) {
                            paramsText.setMargins(
                                20,
                                10,
                                20,
                                innerLayoutArray[j].bottomMargin.toInt()
                            )
                        }
                        if (innerLayoutArray[j].topMargin !== null) {
                            paramsText.setMargins(
                                20,
                                innerLayoutArray[j].topMargin.toInt(),
                                20,
                                10
                            )
                        }

                        when (innerLayoutArray[j].position) {

                            "center" -> {
                                title.gravity = Gravity.CENTER
                                paramsText.gravity = Gravity.CENTER

                            }
                            "left" -> {
                                title.gravity = Gravity.START
                                paramsText.gravity = Gravity.START
                            }
                            "right" -> {
                                title.gravity = Gravity.END
                                paramsText.gravity = Gravity.END
                            }
                        }

                        title.layoutParams = paramsText

                    }

                    AppConstants.VIEWTYPE.BUTTON -> {

                        val button = AppCompatButton(context)
                        button.id = View.generateViewId()
                        button.isAllCaps = false
                        button.text = innerLayoutArray[j].title
                        button.setTextSize(
                            TypedValue.COMPLEX_UNIT_SP,
                            innerLayoutArray[j].fontSize.toFloat()
                        )
                        button.typeface = ResourcesCompat.getFont(context, R.font.opensans_regular)
                        button.setTextColor(Color.parseColor(innerLayoutArray[j].textColor))

                        applyFonts(context, button, innerLayoutArray[j])

                        ViewCompat.setBackgroundTintList(
                            button,
                            ColorStateList.valueOf(Color.parseColor(innerLayoutArray[j].backgroundColor))
                        )

                        val radius = 70 //radius will be 5px
                        val gradientDrawable = GradientDrawable()
                        gradientDrawable.setColor(context.resources.getColor(R.color.orange))
                        gradientDrawable.cornerRadius = radius.toFloat()
                        button.background = gradientDrawable

                        dynamicLayout.addView(button)

                        button.minHeight = 0
                        button.minimumHeight = 0

                        button.height = context.dpToPx(innerLayoutArray[j].height.toInt() + 7)

                        button.textAlignment = View.TEXT_ALIGNMENT_CENTER

                        button.setPadding(1, 10, 1, 10)

                        val params: LinearLayout.LayoutParams
                        if (innerLayoutArray[j].width == "initial") {
                            params = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )

                        } else {

                            params = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )

                        }
                        when (innerLayoutArray[j].position) {

                            "center" -> {
                                params.gravity = Gravity.CENTER
                                button.gravity = Gravity.CENTER
                            }
                            "left" -> {
                                params.gravity = Gravity.START
                                button.gravity = Gravity.CENTER_VERTICAL
                            }
                            "end" -> {
                                params.gravity = Gravity.END
                                button.gravity = Gravity.CENTER_VERTICAL
                            }
                        }
                        button.layoutParams = params


                        params.setMargins(
                            0,
                            innerLayoutArray[j].topMargin.toInt(),
                            0,
                            innerLayoutArray[j].bottomMargin.toInt()
                        )

                        button.layoutParams = params

                        button.textAlignment = View.TEXT_ALIGNMENT_CENTER

                        params.setMargins(
                            0, context.dpToPx(innerLayoutArray[j].topMargin.toInt()), 0,
                            context.dpToPx(innerLayoutArray[j].bottomMargin.toInt())
                        )

                        button.layoutParams = params

                        button.setOnClickListener {
                            tooltip.dismiss()
                            if (index < popArray.size - 1) {
                                index++
                                when (popArray[index].dialogType) {
                                    AppConstants.DIALOG_TYPE.popup -> {
                                        createPopup(context, popArray[index])
                                    }
                                    AppConstants.DIALOG_TYPE.bottom -> {
                                        createPopup(context, popArray[index])
                                    }
                                    else -> {

                                        pingToolTip(context, popArray[index])
                                    }
                                }
                            }
                            if (innerLayoutArray[j].buttonUrl.isNotEmpty()) {
                                Handler(Looper.myLooper()!!).postDelayed({

                                    val URL_REGEX =
                                        "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$"
                                    val p: Pattern = Pattern.compile(URL_REGEX)
                                    val m: Matcher = p.matcher(innerLayoutArray[j].buttonUrl)
                                    if (m.find()) {
                                        val browserIntent =
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse(innerLayoutArray[j].buttonUrl)
                                            )
                                        context.startActivity(browserIntent)
                                    }

                                }, 500)
                            }


                        }

                    }
                }

            }
        }



    }


    private fun createAlertDialog(
        dialogView: PopupLayoutBinding, context: AppCompatActivity,
        outerLayout: RenderModel.OuterLayout,
    ): AlertDialog {

        val builder = AlertDialog.Builder(context)

        builder.setView(dialogView.root)
        alertDialog = builder.create()

        val window: Window = alertDialog!!.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val gravity = when (outerLayout.position) {
            "center" -> {
                Gravity.CENTER
            }
            "left" -> {
                Gravity.START
            }
            "right" -> {
                Gravity.END
            }
            else -> {
                Gravity.BOTTOM
            }
        }

        window.setGravity(gravity)
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 70)
        alertDialog?.window?.setBackgroundDrawable(inset)
        alertDialog?.setCanceledOnTouchOutside(false)

        return alertDialog!!
    }

    fun closeAlertDialog() {
        if (alertDialog != null) {
            alertDialog?.dismiss()
        }
    }

    internal fun applyFonts(
        context: AppCompatActivity, viewType: View,
        model: RenderModel.InnerLayout,
    ) {

        if (model.fontFamily !== null) {
            if (model.fontFamily.contains("Open Sans")) {

                if (viewType is AppCompatTextView) {

                    when (model.fontWeight) {
                        AppConstants.FONT_WEIGHT.REGULAR -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.opensans_regular)

                        }
                        AppConstants.FONT_WEIGHT.BOLD -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.opensans_bold)

                        }
                        AppConstants.FONT_WEIGHT.LIGHT -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.opensans_light)

                        }
                        AppConstants.FONT_WEIGHT.MEDIUM -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.opensans_medium)

                        }
                        AppConstants.FONT_WEIGHT.SEMIBOLD -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.opensans_semibold)

                        }
                        else -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.opensans_regular)

                        }
                    }


                }
                if (viewType is AppCompatButton) {
                    when (model.fontWeight) {
                        AppConstants.FONT_WEIGHT.REGULAR -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.opensans_regular)

                        }
                        AppConstants.FONT_WEIGHT.BOLD -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.opensans_bold)

                        }
                        AppConstants.FONT_WEIGHT.LIGHT -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.opensans_light)

                        }
                        AppConstants.FONT_WEIGHT.MEDIUM -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.opensans_medium)

                        }
                        AppConstants.FONT_WEIGHT.SEMIBOLD -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.opensans_semibold)

                        }
                        else -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.opensans_regular)
                        }
                    }
                }
            } else if (model.fontFamily.contains("Poppins")) {
                if (viewType is AppCompatTextView) {

                    when (model.fontWeight) {
                        AppConstants.FONT_WEIGHT.REGULAR -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.poppins_regular)

                        }
                        AppConstants.FONT_WEIGHT.BOLD -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.poppins_bold)

                        }
                        AppConstants.FONT_WEIGHT.LIGHT -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.poppins_light)

                        }
                        AppConstants.FONT_WEIGHT.MEDIUM -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.poppins_medium)

                        }
                        AppConstants.FONT_WEIGHT.SEMIBOLD -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.poppins_bold)

                        }
                        else -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.poppins_regular)

                        }
                    }


                }
                if (viewType is AppCompatButton) {
                    when (model.fontWeight) {
                        AppConstants.FONT_WEIGHT.REGULAR -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.poppins_regular)

                        }
                        AppConstants.FONT_WEIGHT.BOLD -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.poppins_bold)

                        }
                        AppConstants.FONT_WEIGHT.LIGHT -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.poppins_light)

                        }
                        AppConstants.FONT_WEIGHT.MEDIUM -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.poppins_medium)

                        }
                        AppConstants.FONT_WEIGHT.SEMIBOLD -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.poppins_bold)

                        }
                        else -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.poppins_regular)

                        }
                    }
                }
            } else if (model.fontFamily.contains("Roboto")) {
                if (viewType is AppCompatTextView) {

                    when (model.fontWeight) {
                        AppConstants.FONT_WEIGHT.REGULAR -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.roboto_regular)

                        }
                        AppConstants.FONT_WEIGHT.BOLD -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.roboto_bold)

                        }
                        AppConstants.FONT_WEIGHT.LIGHT -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.roboto_light)

                        }
                        AppConstants.FONT_WEIGHT.MEDIUM -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.roboto_medium)

                        }
                        AppConstants.FONT_WEIGHT.SEMIBOLD -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.roboto_medium)

                        }
                        else -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.roboto_italic)

                        }
                    }


                }
                if (viewType is AppCompatButton) {
                    when (model.fontWeight) {
                        AppConstants.FONT_WEIGHT.REGULAR -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.roboto_regular)

                        }
                        AppConstants.FONT_WEIGHT.BOLD -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.roboto_bold)

                        }
                        AppConstants.FONT_WEIGHT.LIGHT -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.roboto_light)

                        }
                        AppConstants.FONT_WEIGHT.MEDIUM -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.roboto_medium)

                        }
                        AppConstants.FONT_WEIGHT.SEMIBOLD -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.roboto_medium)

                        }
                        else -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.roboto_italic)

                        }
                    }
                }
            } else if (model.fontFamily.contains("circulamedium")) {
                if (viewType is AppCompatTextView) {
                    when (model.fontWeight) {
                        AppConstants.FONT_WEIGHT.REGULAR -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.circula_regular)

                        }
                        AppConstants.FONT_WEIGHT.BOLD -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.circula_bold)

                        }
                        AppConstants.FONT_WEIGHT.LIGHT -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.circula_light)

                        }
                        AppConstants.FONT_WEIGHT.MEDIUM -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.circula_medium)

                        }
                        AppConstants.FONT_WEIGHT.SEMIBOLD -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.circula_medium)

                        }
                        else -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.circula_regular)

                        }
                    }
                }
                if (viewType is AppCompatButton) {
                    when (model.fontWeight) {
                        AppConstants.FONT_WEIGHT.REGULAR -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.circula_regular)

                        }
                        AppConstants.FONT_WEIGHT.BOLD -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.circula_bold)

                        }
                        AppConstants.FONT_WEIGHT.LIGHT -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.circula_light)

                        }
                        AppConstants.FONT_WEIGHT.MEDIUM -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.circula_medium)

                        }
                        AppConstants.FONT_WEIGHT.SEMIBOLD -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.circula_medium)

                        }
                        else -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.circula_regular)

                        }
                    }
                }
            } else {
                if (viewType is AppCompatTextView) {

                    when (model.fontWeight) {
                        AppConstants.FONT_WEIGHT.REGULAR -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.opensans_regular)

                        }
                        AppConstants.FONT_WEIGHT.BOLD -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.opensans_bold)

                        }
                        AppConstants.FONT_WEIGHT.LIGHT -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.opensans_light)

                        }
                        AppConstants.FONT_WEIGHT.MEDIUM -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.opensans_medium)

                        }
                        AppConstants.FONT_WEIGHT.SEMIBOLD -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.opensans_semibold)

                        }
                        else -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.opensans_regular)

                        }
                    }


                }
                if (viewType is AppCompatButton) {
                    when (model.fontWeight) {
                        AppConstants.FONT_WEIGHT.REGULAR -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.opensans_regular)

                        }
                        AppConstants.FONT_WEIGHT.BOLD -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.opensans_bold)

                        }
                        AppConstants.FONT_WEIGHT.LIGHT -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.opensans_light)

                        }
                        AppConstants.FONT_WEIGHT.MEDIUM -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.opensans_medium)

                        }
                        AppConstants.FONT_WEIGHT.SEMIBOLD -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.opensans_semibold)

                        }
                        else -> {
                            viewType.typeface =
                                ResourcesCompat.getFont(context, R.font.opensans_regular)

                        }
                    }
                }
            }
        }

    }

    val tempStep: ArrayList<com.adopshun.render.database.Step> = ArrayList()

    override fun onResponsePopup(jsonObjectModel: JsonObject?) {

        Log.d("ResponseData", jsonObjectModel.toString())
        val jsonObject = JSONObject(jsonObjectModel.toString())
        val data = jsonObject.getString("data")
        if (data != "null") {
            val gson: Gson = GsonBuilder().serializeNulls().create()

            val jsonModel = gson.fromJson(jsonObjectModel, RenderModel::class.java)
            val popupArray = jsonModel.data.identifierDesign
            val tempArray: ArrayList<RenderModel.IdentifierDesign> = ArrayList()


            tempStep.clear()
            Log.d("ResponseDataFIREBASE", jsonModel.gaDetails?.applicationId.toString())
            GlobalScope.launch(Dispatchers.IO) {
                val list = appDb.stepDao().getAll()
                for (i in popupArray?.indices!!) {
                    if (popupArray[i].screenId == mLayout.toString()) {
                        if (!list.contains(Step(popupArray[i].step))) {
                            appDb.stepDao()
                                .insert(com.adopshun.render.database.Step(popupArray[i].step))
                            tempStep.add(com.adopshun.render.database.Step(popupArray[i].step))
                        }
                    }
                }

                for (i in popupArray.indices) {
                    for (j in tempStep.indices) {
                        if (popupArray[i].step == tempStep[j].stepNo) {
                            tempArray.add(popupArray[i])
                        }
                    }
                }

                if (tempArray.size > 0) {
                    mContext?.runOnUiThread {
                        popUp(mContext!!, tempArray)
                    }
                }

            }

        }
    }


}

