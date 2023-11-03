package com.adopshun.render.maintask

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import com.adopshun.render.R
import com.adopshun.render.androidSimpleTooltip.OverlayView
import com.adopshun.render.androidSimpleTooltip.SimpleTooltip
import com.adopshun.render.database.AppDatabase
import com.adopshun.render.database.Step
import com.adopshun.render.databinding.PopupLayoutBinding
import com.adopshun.render.maintask.Extensions.dpToPx
import com.adopshun.render.maintask.Extensions.getAllChildrenViews
import com.adopshun.render.maintask.Extensions.pxToDp
import com.adopshun.render.maintask.Extensions.showLog
import com.adopshun.render.maintask.Extensions.showToast
import com.adopshun.render.model.ApiInterfaceModel
import com.adopshun.render.model.RenderModel
import com.adopshun.render.model.SegmentModel
import com.adopshun.render.retrofit.RetrofitService
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Matcher
import java.util.regex.Pattern

object RenderPopup : ApiInterfaceModel.OnApiResponseListener {

    @JvmStatic
    var index = 0

    @JvmStatic
    private var mLayout = 0

    @JvmStatic
    var mContext: AppCompatActivity? = null

    @JvmStatic
    var popArray: ArrayList<RenderModel.IdentifierDesign> = ArrayList()

    @JvmStatic
    private lateinit var appDb: AppDatabase

    @JvmStatic
    @SuppressLint("StaticFieldLeak")
    var tooltip: SimpleTooltip? = null


    @JvmStatic
    @JvmOverloads
    @SuppressLint("InvalidAnalyticsName")
    fun checkFirstRun(
        context: AppCompatActivity,
        layout: Int,
        userId: String? = null,
        segmentId: Int? = null
    ) {
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
            appDb = AppDatabase.getDatabase(context)
            GlobalScope.launch(Dispatchers.IO) {
                appDb.stepDao().clearStep()
            }

            Handler(Looper.myLooper()!!).postDelayed({
                index = 0
                ApiInterfaceModel.instance!!.setListener(this)
                getPops(context, context.packageName, userId, segmentId)
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


    @JvmStatic
    @JvmOverloads
    fun showPopups(
        context: AppCompatActivity,
        layout: Int,
        userId: String? = null,
        segmentId: Int? = null
    ) {
        if (context.getSharedPreferences("renderFirstPop", MODE_PRIVATE)
                ?.getBoolean(AppConstants.IS_FIRST_RUN, false) == false
        ) {
            checkFirstRun(context, layout, userId, segmentId)
        }
        if (context.getSharedPreferences("renderFirstPop", MODE_PRIVATE)
                ?.getBoolean(AppConstants.IS_POP_STATUS, false) == true
        ) {
            mLayout = layout
            mContext = context
            appDb = AppDatabase.getDatabase(context)

            Handler(Looper.myLooper()!!).postDelayed({
                index = 0
                ApiInterfaceModel.instance?.setListener(this)
                getPops(context, context.packageName, userId, segmentId)
            }, 500)
        }

    }

    @JvmOverloads
    @JvmStatic
    private fun getPops(
        context: AppCompatActivity,
        applicationId: String,
        userId: String? = null,
        segmentId: Int? = null
    ) {

        showLog(applicationId)
        val service = RetrofitService.getInstance(context)
        if (userId.isNullOrEmpty() && segmentId == null) {
            service.getJson(applicationId).enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val json = response.body()
                        ApiInterfaceModel.instance?.apiCall(json)

                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    showToast(context, t.toString())
                }
            })
        } else if (userId?.isNotEmpty() == true && segmentId != null) {
            service.getJsonWithUserId(applicationId, userId.toString())
                .enqueue(object : Callback<JsonObject> {
                    override fun onResponse(
                        call: Call<JsonObject>,
                        response: Response<JsonObject>
                    ) {
                        if (response.isSuccessful) {
                            val json = response.body()
                            ApiInterfaceModel.instance?.apiCall(json)
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        showToast(context, t.toString())
                    }

                })
        } else {
            if (userId.isNullOrEmpty() && segmentId != null) {
                showAlertDialog(context, "Required", "user id is null or empty", "Ok") {

                }
            }
            if (userId?.isNotEmpty() == true && segmentId == null) {
                showAlertDialog(context, "Required", "segment id is empty", "Ok") {

                }
            }
        }


    }

    @JvmStatic
    fun storeSegmentData(
        context: AppCompatActivity,
        segmentModel: SegmentModel,
        authToken: String
    ) {
        if (context.getSharedPreferences("renderFirstPop", MODE_PRIVATE)
                ?.getBoolean(AppConstants.IS_POP_STATUS, false) == true
        ) {
            val segmentService = RetrofitService.getInstance(context)

            showLog(Gson().toJson(segmentModel))
            if (segmentModel.segment_id != null && segmentModel.fields.isNotEmpty() && !segmentModel.unique_project_id.isNullOrEmpty()) {
                segmentService.requestCatcher("Bearer $authToken", segmentModel)
                    .enqueue(object : Callback<String> {
                        override fun onResponse(call: Call<String>, response: Response<String>) {

                            val rawResponse = response.raw().body.toString()
                            showLog("Raw Response: $rawResponse")

                            // Handle the response here
                            if (response.isSuccessful) {
                                // Parse the JSON response
                                // ...
                                showLog("Parse the JSON response ${response.body().toString()}")
                            } else {
                                // Handle the error
                                showLog("Handle the error")
                            }
                        }

                        override fun onFailure(call: Call<String>, t: Throwable) {
                            showLog(t.toString())
                        }

                    })
            } else {
                // Show toast messages for each condition not met
                if (segmentModel.segment_id == null) {
                    showAlertDialog(context, "Segment", "segment id is null.", "Ok") {

                    }
                }
                if (segmentModel.fields.isEmpty()) {
                    showAlertDialog(context, "Segment", "fields is empty", "Ok") {

                    }
                }
                if (segmentModel.unique_project_id.isNullOrEmpty()) {
                    showAlertDialog(
                        context,
                        "Segment",
                        "unique_project_id is null or empty",
                        "Ok"
                    ) {

                    }
                }
            }
        }

    }

    @JvmStatic
    private fun showAlertDialog(
        context: Context,
        title: String,
        message: String,
        positiveButtonText: String,
        onPositiveClick: () -> Unit
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(positiveButtonText) { dialog, which ->
                // Call the provided positive button response
                onPositiveClick()
            }
            .show()
    }

    @JvmStatic
    private fun popUp(
        context: AppCompatActivity,
        identifierDesign: ArrayList<RenderModel.IdentifierDesign>
    ) {
        popArray = identifierDesign
        val mViewGroup = getRootViewGroup(context)
        val originalLayoutParams = mViewGroup.layoutParams
        val originalGravity = (mViewGroup.layoutParams as? FrameLayout.LayoutParams)?.gravity

        when (popArray[index].dialogType) {
            AppConstants.DIALOG_TYPE.popup,AppConstants.DIALOG_TYPE.bottom  -> {
                val identifierDesign: RenderModel.IdentifierDesign = popArray[index]
                showCustomDialog(context, identifierDesign, originalLayoutParams, originalGravity, mViewGroup)
            }
            else -> {
                showToolTip(
                    context,
                    popArray[index],
                    originalLayoutParams,
                    originalGravity,
                    mViewGroup
                )
            }
        }
    }

    @JvmStatic
    private fun getRootViewGroup(context: AppCompatActivity): ViewGroup {
        return (context.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
    }

    @JvmStatic
    private fun findViewByTag(viewsList: List<View>, tag: String): View? {
        return viewsList.firstOrNull { it.tag == tag }
    }

    @JvmStatic
    private fun createLayoutParams(
        gravity: Int,
        rootLinear: View,
        skipButton: View
    ): RelativeLayout.LayoutParams {
        val rootParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        if (gravity == Gravity.BOTTOM) {
            rootParams.addRule(RelativeLayout.BELOW, rootLinear.id)
            rootParams.setMargins(0, 50, 50, 50)
            skipButton.layoutParams = rootParams
        } else {
            // Handle other cases
            val rootParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            rootParams.addRule(RelativeLayout.BELOW, skipButton.id)
            rootParams.setMargins(0, 50, 0, 0)
            rootLinear.layoutParams = rootParams
        }
        return rootParams
    }


    @JvmStatic
    @SuppressLint("Range")
    internal fun showToolTip(
        context: AppCompatActivity,
        identifierDesign: RenderModel.IdentifierDesign,
        originalLayoutParams: ViewGroup.LayoutParams?,
        originalGravity: Int?, parentViewGroup: ViewGroup
    ) {
        try {
            // Inflate the layout using data binding
            val binding = PopupLayoutBinding.inflate(LayoutInflater.from(context))
            // Extract data from identifierDesign
            val outerLayout = identifierDesign.outerLayout
            val belongId = outerLayout.belongId
            val innerLayoutArray = identifierDesign.innerLayout
            val viewsList = parentViewGroup.getAllChildrenViews()

            binding.apply {
                // Initialize view IDs starting from 100
                var counter2 = 100
                for (i in 0 until viewsList.size) {
                    viewsList[i].tag = "$counter2"
                    counter2++
                }
                // Find the anchor view by tag
                val anchorView = findViewByTag(viewsList, belongId)
                val anchorTag = anchorView?.tag.toString()
                if (anchorTag == belongId) {
                    // Calculate the gravity based on anchor view and mViewGroup
                    val gravity = calculateGravity(parentViewGroup, anchorView, context)

                    // Create layout parameters for the tooltip container
                    createLayoutParams(gravity, linearRoot, btnSkip)

                    // Set up views within the tooltip
                    setupViews(
                        innerLayoutArray,
                        dynamicLayout,
                        context,
                        root,
                        originalLayoutParams,
                        originalGravity, parentViewGroup
                    )

                    // Create and show the tooltip
                    tooltip =
                        createTooltip(
                            context,
                            anchorView,
                            gravity,
                            root,
                            btnSkip.id,
                            parentViewGroup,
                            originalLayoutParams,
                            originalGravity
                        )
                    tooltip?.show()

                    // Set click listeners for skip and cancel buttons
                    btnSkip.setOnClickListener {
                        root.visibility = View.GONE
                        if (tooltip?.isShowing == true) {
                            tooltip?.dismiss()
                            parentViewGroup.removeView(binding.root)
                        }
                    }
                    btnCancel.setOnClickListener {
                        root.visibility = View.GONE
                        if (tooltip?.isShowing == true) {
                            tooltip?.dismiss()
                            parentViewGroup.removeView(binding.root)
                        }
                        isNextDialogTypeExist(
                            context,
                            originalLayoutParams,
                            originalGravity,
                            parentViewGroup, binding.root
                        )
                    }
                } else {
                    Handler(Looper.myLooper()!!).postDelayed({
                        isNextDialogTypeExist(
                            context,
                            originalLayoutParams,
                            originalGravity,
                            parentViewGroup,
                            binding.root
                        )
                    }, 500)
                }
            }
        } catch (ex: java.lang.IndexOutOfBoundsException) {
            Log.e("PingToolTip", "IndexOutOfBoundsException occurred: ${ex.message}")
        } catch (ex: NullPointerException) {
            Log.e("PingToolTip", "NullPointerException occurred: ${ex.message}")
        } catch (ex: Exception) {
            Log.e("PingToolTip", "An error occurred: ${ex.message}")
        }
    }


    @JvmStatic
    private fun resetLayoutAndDismissTooltip(
        mViewGroup: ViewGroup,
        context: AppCompatActivity,
        originalLayoutParams: ViewGroup.LayoutParams?,
        originalGravity: Int?
    ) {
        Log.d("ResetLayout", "ResetLayoutAndDismissTooltip called")
        // Run UI-related code on the main thread
        context.runOnUiThread {
            originalLayoutParams?.let {
                mViewGroup.layoutParams = it
                if (it is FrameLayout.LayoutParams && originalGravity != null) {
                    it.gravity = originalGravity
                }
            }
        }
        Log.d("ResetLayout", "ResetLayoutAndDismissTooltip completed")
    }

    @JvmStatic
    @SuppressLint("Range")
    private fun setupViews(
        innerLayoutArray: List<RenderModel.InnerLayout>,
        dynamicLayout: LinearLayout,
        context: AppCompatActivity,
        tooltipContent: View,
        originalLayoutParams: ViewGroup.LayoutParams?,
        originalGravity: Int?, parentViewGroup: ViewGroup
    ) {
        for (j in innerLayoutArray.indices) {

            when (innerLayoutArray[j].type) {

                AppConstants.VIEWTYPE.LABEL -> {
                    val title = AppCompatTextView(context)
                    title.id = View.generateViewId()
                    title.text = innerLayoutArray[j].title

                    title.setTextSize(
                        TypedValue.COMPLEX_UNIT_SP,
                        innerLayoutArray[j].fontSize.toFloat()
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
                    button.typeface =
                        ResourcesCompat.getFont(context, R.font.opensans_regular)
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
                        tooltipContent.visibility = View.GONE
                        if (tooltip?.isShowing == true) {
                            tooltip?.dismiss()
                            parentViewGroup.removeView(tooltipContent)
                        }
                        isNextDialogTypeExist(
                            context,
                            originalLayoutParams,
                            originalGravity,
                            parentViewGroup, tooltipContent
                        )
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

    @JvmStatic
    private fun createTooltip(
        context: AppCompatActivity,
        anchorView: View?,
        gravity: Int,
        contentView: View,
        skipButtonId: Int,
        mViewGroup: ViewGroup, originalLayoutParams: ViewGroup.LayoutParams?,
        originalGravity: Int?
    ): SimpleTooltip? {
        return SimpleTooltip.Builder(context)
            .anchorView(anchorView)
            .gravity(gravity)
            .dismissOnOutsideTouch(false)
            .dismissOnInsideTouch(false)
            .modal(true)
            .onDismissListener {
                resetLayoutAndDismissTooltip(
                    mViewGroup,
                    context,
                    originalLayoutParams,
                    originalGravity
                )
            }
            .setWidth(mViewGroup.width)
            .transparentOverlay(false)
            .highlightShape(OverlayView.HIGHLIGHT_SHAPE_RECTANGULAR)
            .cornerRadius(20f)
            .overlayOffset(0f)
            .contentView(contentView, skipButtonId)
            .focusable(true)
            .build()

    }

    @JvmStatic
    private fun calculateGravity(
        mViewGroup: ViewGroup,
        anchorView: View?,
        context: AppCompatActivity
    ): Int {
        val rectf = Rect()
        anchorView?.getLocalVisibleRect(rectf)
        anchorView?.getGlobalVisibleRect(rectf)

        val offsetViewBounds = Rect()
        anchorView?.getDrawingRect(offsetViewBounds)
        mViewGroup.offsetDescendantRectToMyCoords(anchorView, offsetViewBounds)

        val relativeTop = offsetViewBounds.top


        val centerPoint = context.pxToDp(mViewGroup.height / 2)
        val viewY = context.pxToDp(relativeTop)
        val params: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        params.setMargins(50, 10, 50, 10)

        return if (viewY < centerPoint) {
            Gravity.BOTTOM  //Tooltip gravity
        } else {
            Gravity.TOP  //Tooltip gravity
        }
    }
    @JvmStatic
    private fun showCustomDialog(context: AppCompatActivity, identifierDesign: RenderModel.IdentifierDesign, originalLayoutParams: ViewGroup.LayoutParams?, originalGravity: Int?, mViewGroup: ViewGroup) {
        val dialogFragment = CustomDialogFragment(
            identifierDesign,
            context,
            originalLayoutParams,
            originalGravity,
            mViewGroup
        )
        dialogFragment.show(context.supportFragmentManager, "custom_dialog")
    }
    @JvmStatic
    private fun isNextDialogTypeExist(
        context: AppCompatActivity, originalLayoutParams: ViewGroup.LayoutParams?,
        originalGravity: Int?, mViewGroup: ViewGroup, tooltipContent: View
    ) {
        if (index < popArray.size - 1) {
            index++
            when (popArray[index].dialogType) {
                AppConstants.DIALOG_TYPE.popup, AppConstants.DIALOG_TYPE.bottom -> {
                    val identifierDesign: RenderModel.IdentifierDesign = popArray[index]
                    showCustomDialog(context, identifierDesign, originalLayoutParams, originalGravity, mViewGroup)
                }
                else -> {
                    showToolTip(
                        context,
                        popArray[index],
                        originalLayoutParams,
                        originalGravity,
                        mViewGroup
                    )
                }
            }
        } else {
            if (tooltip?.isShowing == true) {
                tooltip?.dismiss()
                mViewGroup.removeView(tooltipContent)

            }
        }
    }


    @JvmStatic
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

    @JvmStatic
    private val tempStep: ArrayList<com.adopshun.render.database.Step> = ArrayList()

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
                        if (!list.contains(Step(popupArray[i].step.toString()))) {
                            appDb.stepDao()
                                .insert(com.adopshun.render.database.Step(popupArray[i].step.toString()))
                            tempStep.add(com.adopshun.render.database.Step(popupArray[i].step.toString()))
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

