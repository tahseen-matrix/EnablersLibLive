package com.adopshun.render.maintask

/**
 * Created By Matrix Marketers
 */
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.DialogFragment
import com.adopshun.render.R
import com.adopshun.render.maintask.Extensions.dpToPx
import com.adopshun.render.maintask.Extensions.loadGifImage
import com.adopshun.render.maintask.Extensions.loadImage
import com.adopshun.render.maintask.RenderPopup.index
import com.adopshun.render.maintask.RenderPopup.pingToolTip
import com.adopshun.render.maintask.RenderPopup.popArray
import com.adopshun.render.model.RenderModel
import java.util.regex.Matcher
import java.util.regex.Pattern

class CustomDialogFragment(
    private val identifierDesign: RenderModel.IdentifierDesign,
    val context: AppCompatActivity
) : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.MyDialogStyle)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dialogView = inflater.inflate(R.layout.popup_layout_bottom, container, false)
        populateDialogView(dialogView)
        return dialogView
    }

    private fun resetLayoutAndDismissDialog(
        mViewGroup: ViewGroup,
        originalLayoutParams: ViewGroup.LayoutParams?,
        originalGravity: Int?
    ) {
        mViewGroup.layoutParams = originalLayoutParams
        if (originalLayoutParams is FrameLayout.LayoutParams && originalGravity != null) {
            originalLayoutParams.gravity = originalGravity
        }
    }
    @SuppressLint("Range")
    private fun populateDialogView(dialogView: View) {
        // Populate dialog views using the identifierDesign data
        val innerLayoutArray = identifierDesign.innerLayout
        val outerLayout = identifierDesign.outerLayout
        val rootLinear: LinearLayout = dialogView.findViewById(R.id.linearRoot)
        val rootRelative = dialogView.findViewById<RelativeLayout>(R.id.relativeRoot)
        val skipButton = dialogView.findViewById<AppCompatTextView>(R.id.btnSkip)
        val closeButton = dialogView.findViewById<AppCompatImageView>(R.id.btnCancel)


        val mViewGroup = RenderPopup.getRootViewGroup(context)
        // Keep track of the original state of mViewGroup
        val originalLayoutParams = mViewGroup.layoutParams
        val originalGravity =
            (mViewGroup.layoutParams as? FrameLayout.LayoutParams)?.gravity

        // Modify the mViewGroup layout parameters when creating the popup
        val popupLayoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        mViewGroup.layoutParams = popupLayoutParams

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

        val answerLayoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        rootLinear.layoutParams = answerLayoutParams

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
                    title.textSize = innerLayout.fontSize.toFloat()

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
                    button.textSize = innerLayout.fontSize.toFloat()

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
                        dialogView.visibility = View.GONE
                        dismiss()
                        if (index < popArray.size - 1) {
                            index++
                            when (popArray[index].dialogType) {
                                AppConstants.DIALOG_TYPE.popup -> {
                                    dismiss()
                                    val identifierDesign: RenderModel.IdentifierDesign =
                                        popArray[index] // Your identifier design data
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
                                        popArray[index] // Your identifier design data
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
                                    dismiss()
                                    pingToolTip(
                                        context,
                                        popArray[index]
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

        closeButton.setOnClickListener {
            resetLayoutAndDismissDialog(
                mViewGroup,
                originalLayoutParams,
                originalGravity
            )
            if (index < popArray.size - 1) {
                index++
                when (popArray[index].dialogType) {
                    AppConstants.DIALOG_TYPE.popup -> {
                        val identifierDesign: RenderModel.IdentifierDesign =
                            popArray[index] // Your identifier design data
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
                        dismiss()
                        val identifierDesign: RenderModel.IdentifierDesign =
                            popArray[index] // Your identifier design data
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
                        dismiss()
                        pingToolTip(context, popArray[index])
                    }
                }
            }
            else{
                resetLayoutAndDismissDialog(
                    mViewGroup,
                    originalLayoutParams,
                    originalGravity
                )
                dismiss()
            }
        }
        skipButton.setOnClickListener {
            resetLayoutAndDismissDialog(
                mViewGroup,
                originalLayoutParams,
                originalGravity
            )
            dismiss()
        }
    }
}
