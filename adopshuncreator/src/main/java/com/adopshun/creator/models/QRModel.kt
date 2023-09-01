package com.adopshun.creator.models

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class QRModel {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null
}