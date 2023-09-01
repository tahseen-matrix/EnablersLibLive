package com.adopshun.creator.models

class CustomModel private constructor() {
    interface OnCustomStateListener {
        fun onQrScan(user_id: String?, unique_id: String?, session_id: String?, project_id: String){
            System.out.println("Default implementation of the display method");
        }
        fun onApiCall(message: String?)
    }

    private var mListener: OnCustomStateListener? = null
    var state: String? = null
        private set

    fun setListener(listener: OnCustomStateListener?) {
        mListener = listener
    }

    fun onQrScan(user_id: String?, unique_id: String, session_id: String, project_name: String) {
        if (mListener != null) {
            state = user_id
            qrCallState(user_id, unique_id, session_id,project_name)
        }
    }

    fun apiCall(message: String) {
        if (mListener != null) {
            onApiCallState(message)
        }
    }

    private fun qrCallState(
        user_id: String?,
        unique_id: String,
        session_id: String,
        project_name: String
    ) {
        mListener!!.onQrScan(user_id, unique_id, session_id,project_name)
    }

    private fun onApiCallState(message: String) {
        mListener!!.onApiCall(message)
    }

    companion object {
        private var mInstance: CustomModel? = null
        val instance: CustomModel?
            get() {
                if (mInstance == null) {
                    mInstance = CustomModel()
                }
                return mInstance
            }
    }
}