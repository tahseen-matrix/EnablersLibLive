package com.adopshun.render.maintask

object AppConstants{

    const val BASE_URL = "https://adopshun.com/api/"// ""https://adopshun.matrixm.io/api/"

    const val DESCRIPTION: String = "description"

    const val IS_POPPED_UP: String = "isPoppedUp"
    const val IS_COMPLETED: String = "isCompleted"
    const val IDENTIFIER_OLD_SIZE = "oldSize"
    const val IS_POP_STATUS:String = "isPopUp"
    const val IS_FIRST_RUN:String = "isFirstRun"

    object POPUPTYPE{
        const val POP_UP = 1
        const val TOOLTIP = 2
        const val BOTTOM_SHEET = 3
        const val TRANSPARENT = 4
    }
    object VIEWTYPE{
        const val IMAGE = "image"
        const val LABEL = "label"
        const val BUTTON = "button"
    }

    object DIALOG_TYPE{
        const val popup = 1
        const val bottom = 2
        const val ping = 3
    }

    object FONT_WEIGHT{
        const val BOLD = "700"
        const val REGULAR = "400"
        const val LIGHT = "300"
        const val MEDIUM = "500"
        const val SEMIBOLD = "600"
    }


}