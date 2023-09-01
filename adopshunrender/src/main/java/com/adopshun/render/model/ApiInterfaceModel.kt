package com.adopshun.render.model

import android.view.ViewGroup
import com.google.gson.JsonObject

class ApiInterfaceModel private constructor() {
    interface OnApiResponseListener {
        fun onResponsePopup(message: JsonObject?,   viewGroup: ViewGroup)
    }

    private var mListener: OnApiResponseListener? = null
    var state: String? = null
        private set

    fun setListener(listener: OnApiResponseListener?) {
        mListener = listener
    }


    fun apiCall(message: JsonObject?,   viewGroup: ViewGroup) {
        if (mListener != null) {
            onApiCallState(message,   viewGroup)
        }
    }


    private fun onApiCallState(message: JsonObject?,   viewGroup: ViewGroup) {
        mListener!!.onResponsePopup(message,   viewGroup)
    }

    companion object {
        private var mInstance: ApiInterfaceModel? = null
        val instance: ApiInterfaceModel?
            get() {
                if (mInstance == null) {
                    mInstance = ApiInterfaceModel()
                }
                return mInstance
            }
    }
}