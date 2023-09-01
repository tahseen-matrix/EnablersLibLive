package com.adopshun.creator.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class JsonModel {

    @SerializedName("meta_data")
    @Expose
    var metaData: MetaData = MetaData()

    @SerializedName("image")
    @Expose
    var image: String = ""


    inner class MetaData {
        @SerializedName("screen_size_height")
        @Expose
        var screenSizeHeight: String = ""

        @SerializedName("screen_id")
        @Expose
        var screen_id: String = ""

        @SerializedName("controllers")
        @Expose
        var controllers: MutableList<Controller> = emptyList<Controller>().toMutableList()

        @SerializedName("screen_size_width")
        @Expose
        var screenSizeWidth: String = ""

    }

    inner class Controller {

        @SerializedName("id")
        @Expose
        var id: String = ""

        @SerializedName("height")
        @Expose
        var height: String = ""

        @SerializedName("width")
        @Expose
        var width: String = ""

        @SerializedName("point_y")
        @Expose
        var pointY: String = ""

        @SerializedName("point_x")
        @Expose
        var pointX: String = ""

        @SerializedName("title")
        @Expose
        var title: String = ""

        override fun toString(): String {
            return "Controller(id='$id', height='$height', width='$width', pointY='$pointY', pointX='$pointX')"
        }


    }

}