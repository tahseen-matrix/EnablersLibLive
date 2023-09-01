package com.adopshun.render.maintask

/**
 * Created By Matrix Marketers
 */
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.adopshun.render.R
import com.adopshun.render.model.RenderModel

class CustomDialogFragment(
    private val identifierDesign: RenderModel.IdentifierDesign,
    val context: AppCompatActivity
) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dialogView = inflater.inflate(R.layout.popup_layout, container, false)


      //  populateDialogView(dialogView)
        return dialogView
    }
/*
    @SuppressLint("Range")
    private fun populateDialogView(dialogView: View) {
        // Populate dialog views using the identifierDesign data
        val innerLayoutArray = identifierDesign.innerLayout
        val outerLayout = identifierDesign.outerLayout
        val belongId = outerLayout.belongId
        val rootLinear: LinearLayout = dialogView.findViewById(R.id.linearRoot)
        val rootRelative = dialogView.findViewById<RelativeLayout>(R.id.relativeRoot)
        val skipButton = dialogView.findViewById<AppCompatTextView>(R.id.btnSkip)
        val closeButton = dialogView.findViewById<AppCompatImageView>(R.id.btnCancel)
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

        val viewsList = dialogView.getAllChildrenViews()

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

        val offsetViewLocation = IntArray(2)

        // Get the location of the anchor view in window coordinates
        anchorView.getLocationInWindow(offsetViewLocation)

        val offsetViewBounds = Rect()
        offsetViewBounds.left = offsetViewLocation[0]
        offsetViewBounds.top = offsetViewLocation[1]
        offsetViewBounds.right = offsetViewBounds.left + anchorView.width
        offsetViewBounds.bottom = offsetViewBounds.top + anchorView.height


        val relativeTop = offsetViewBounds.top

        val centerPoint = context.pxToDp(dialogView.height / 2)
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

        for (innerLayout in innerLayoutArray) {
            when (innerLayout.type) {
                AppConstants.VIEWTYPE.IMAGE -> {
                    // Create and add ImageView
                    // Load image using innerLayout.imageUrl
                    val image = AppCompatImageView(requireContext())
                    image.id = View.generateViewId()

                    val height =
                        context.dpToPx(innerLayout.height.toInt() + 15).toString()
                    val width = context.dpToPx(innerLayout.width.toInt() + 15).toString()

                    innerLayout.imageUrl.let {
                        it.let { it1 ->
                            if (Extensions.isImageFile(it1)) {
                                image.loadGifImage(it1, image, width, height)
                            } else image.loadImage(it1, image, width, height)
                        }
                    }


                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )


                    if (innerLayout.bottomMargin !== null) {
                        params.setMargins(
                            20,
                            -50,
                            20,
                            10
                        )
                    }

                    if (innerLayout.topMargin !== null) {
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
                    // Create and add TextView
                    // Set text, font size, font color, etc. using innerLayout data

                    val title = AppCompatTextView(context)
                    title.id = View.generateViewId()
                    title.text = innerLayout.title
                    title.textSize = innerLayout.fontSize!!.toFloat()

                    RenderPopup.applyFonts(context, title, innerLayout)

                    title.setTextColor(Color.parseColor(innerLayout.textColor))

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


                    if (innerLayout.fontSize.toInt() < 6) {
                        title.width = 450
                        title.gravity = Gravity.CENTER
                    }

                    when (innerLayout.position) {

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
                    // Create and add Button
                    // Set text, font size, background color, etc. using innerLayout data
                    // Set click listener to handle button click
                    val button = AppCompatButton(context)
                    button.id = View.generateViewId()
                    button.isAllCaps = false
                    button.text = innerLayout.title
                    button.textSize = innerLayout.fontSize!!.toFloat()

                    button.typeface = ResourcesCompat.getFont(context, R.font.opensans_regular)
                    button.setTextColor(Color.parseColor(innerLayout.textColor))

                    RenderPopup.applyFonts(context, button, innerLayout)

                    ViewCompat.setBackgroundTintList(
                        button,
                        ColorStateList.valueOf(Color.parseColor(innerLayout.backgroundColor))
                    )

                    val radius = 70 //radius will be 5px
                    val gradientDrawable = GradientDrawable()
                    gradientDrawable.setColor(context.resources.getColor(R.color.orange))
                    gradientDrawable.cornerRadius = radius.toFloat()
                    button.background = gradientDrawable

                    rootLinear.addView(button)

                    button.minHeight = 0
                    button.minimumHeight = 0

                    button.height = context.dpToPx(innerLayout.height.toInt() + 7)

                    button.setPadding(25, 10, 25, 10)
                    val params: LinearLayout.LayoutParams
                    if (innerLayout.width == "initial") {
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
                    when (innerLayout.position) {

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
                        30, context.dpToPx(innerLayout.topMargin.toInt() + 10), 30,
                        context.dpToPx(innerLayout.bottomMargin.toInt() + 10)
                    )

                    button.layoutParams = params

                    button.setOnClickListener {
                        *//* val bundle = Bundle()
                         bundle.putString(FirebaseAnalytics.Param.METHOD, "POP UP CLICK")
                         firebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, bundle)*//*
                        dialogView.visibility = View.GONE
                        dismiss()
                        if (RenderPopup.index < RenderPopup.popArray.size - 1) {
                            RenderPopup.index++
                            when (RenderPopup.popArray[RenderPopup.index].dialogType) {
                                AppConstants.DIALOG_TYPE.popup -> {
                                    dismiss()
                                    val identifierDesign: RenderModel.IdentifierDesign =
                                        RenderPopup.popArray[RenderPopup.index] // Your identifier design data
                                    val dialogFragment = CustomDialogFragment(
                                        identifierDesign,
                                        RenderPopup.mContext!!
                                    )
                                    dialogFragment.show(
                                        RenderPopup.mContext?.supportFragmentManager!!,
                                        "custom_dialog"
                                    )
                                }
                                AppConstants.DIALOG_TYPE.bottom -> {
                                    // When you want to show the dialog
                                    dismiss()
                                    val identifierDesign: RenderModel.IdentifierDesign =
                                        RenderPopup.popArray[RenderPopup.index] // Your identifier design data
                                    val dialogFragment = CustomDialogFragment(
                                        identifierDesign,
                                        RenderPopup.mContext!!
                                    )
                                    dialogFragment.show(
                                        RenderPopup.mContext?.supportFragmentManager!!,
                                        "custom_dialog"
                                    )
                                }
                                else -> {
                                    RenderPopup.pingToolTip(
                                        context,
                                        RenderPopup.popArray[RenderPopup.index]
                                    )
                                }
                            }
                        }
                        if (innerLayout.buttonUrl.isNotEmpty()) {
                            Handler(Looper.myLooper()!!).postDelayed({
                                val URL_REGEX =
                                    "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$"
                                val p: Pattern = Pattern.compile(URL_REGEX)
                                val m: Matcher = p.matcher(innerLayout.buttonUrl)
                                if (m.find()) {
                                    val browserIntent =
                                        Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(innerLayout.buttonUrl)
                                        )
                                    context.startActivity(browserIntent)
                                }
                            }, 500)
                        }


                    }
                }
            }
        }
    }*/
}
