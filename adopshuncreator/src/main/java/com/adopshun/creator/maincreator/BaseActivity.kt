package com.adopshun.creator.maincreator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.adopshun.creator.utils.PreferencesManager

open class BaseActivity: AppCompatActivity() {

    var sessionManager : PreferencesManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PreferencesManager.initializeInstance(context = this)
        sessionManager = PreferencesManager.instance!!

    }
}