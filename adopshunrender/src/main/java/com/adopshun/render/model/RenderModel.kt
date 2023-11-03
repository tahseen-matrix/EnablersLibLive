package com.adopshun.render.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RenderModel {

    @SerializedName("status")
    @Expose
     val status: String = ""

    @SerializedName("message")
    @Expose
     val message: String = ""

    @SerializedName("data")
    @Expose
    var data: Data = Data()

    @SerializedName("ga_details")
    @Expose
    val gaDetails: GaDetails? = null

    inner class GaDetails {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("project_id")
        @Expose
        var projectId: Int? = null

        @SerializedName("type")
        @Expose
        var type: Int? = null

        @SerializedName("api_key")
        @Expose
        var apiKey: String? = null

        @SerializedName("bundle_id")
        @Expose
        var bundleId: String? = null

        @SerializedName("client_id")
        @Expose
        var clientId: String? = null

        @SerializedName("gcm_sender_id")
        @Expose
        var gcmSenderId: String? = null

        @SerializedName("google_app_id")
        @Expose
        var googleAppId: String? = null

        @SerializedName("app_project_id")
        @Expose
        var appProjectId: String? = null

        @SerializedName("storage_bucket")
        @Expose
        var storageBucket: String? = null

        @SerializedName("application_id")
        @Expose
        var applicationId: String? = null

        @SerializedName("database_url")
        @Expose
        var databaseUrl: String? = null

        @SerializedName("created_at")
        @Expose
        var createdAt: String? = null

        @SerializedName("updated_at")
        @Expose
        var updatedAt: String? = null
    }
    inner class Data {
        @SerializedName("unique_project_id")
        @Expose
         val uniqueProjectId: String = ""

        @SerializedName("identifier_design")
        @Expose
         val identifierDesign: List<IdentifierDesign> = emptyList()

    }

    inner class IdentifierDesign {
        @SerializedName("dialog_type")
        @Expose
         val dialogType: Int = 0

        @SerializedName("screen_id")
        @Expose
         val screenId: String = ""

        @SerializedName("step")
        @Expose
         val step: Any? = null

        @SerializedName("outer_layout")
        @Expose
         val outerLayout: OuterLayout = OuterLayout()

        @SerializedName("inner_layout")
        @Expose
         val innerLayout: List<InnerLayout> = emptyList()

    }

    inner class OuterLayout{
        @SerializedName("position")
        @Expose
         val position: String = ""

        @SerializedName("belong_id")
        @Expose
         val belongId: String = ""

        @SerializedName("background_color")
        @Expose
         val backgroundColor: String = ""

        @SerializedName("height")
        @Expose
         val height: String = ""

        @SerializedName("width")
        @Expose
         val width: String = ""

    }

    inner class InnerLayout{
        @SerializedName("top_margin")
        @Expose
         val topMargin: String = ""

        @SerializedName("bottom_margin")
        @Expose
         val bottomMargin: String = ""

        @SerializedName("imageUrl")
        @Expose
         val imageUrl: String = ""

        @SerializedName("height")
        @Expose
         val height: String = ""

        @SerializedName("width")
        @Expose
         val width: String = ""

        @SerializedName("type")
        @Expose
         val type: String = ""

        @SerializedName("title")
        @Expose
         val title: String = ""

        @SerializedName("font_family")
        @Expose
         val fontFamily: String = ""

        @SerializedName("font_size")
        @Expose
         val fontSize: String = ""

        @SerializedName("font_weight")
        @Expose
         val fontWeight: String = ""

        @SerializedName("position")
        @Expose
         val position: String = ""

        @SerializedName("text_color")
        @Expose
         val textColor: String = ""

        @SerializedName("background_color")
        @Expose
        val backgroundColor: String = ""

        @SerializedName("buttonUrl")
        @Expose
        val buttonUrl: String = ""



    }

}