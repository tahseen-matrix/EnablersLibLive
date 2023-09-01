package com.adopshun.render.maintask

import android.content.Context
import android.content.SharedPreferences
import kotlin.jvm.Synchronized

class PreferencesManager  constructor(context: Context) {
    private val mPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    fun setLongValue(KEY: String?, value: Long) {
        mPref.edit()
            .putLong(KEY, value)
            .commit()
    }

    fun getLongValue(KEY: String?): Long {
        return mPref.getLong(KEY, 0)
    }

    fun getIntValue(KEY: String?): Int? {
        return mPref.getInt(KEY, 0)
    }


    fun setInt(KEY: String?, value: Int?) {
        mPref.edit()
            .putInt(KEY, value!!)
            .apply()
    }

    fun getBooleanValue(KEY: String?): Boolean? {
        return mPref.getBoolean(KEY, false)
    }


    fun setBoolean(KEY: String?, value: Boolean?) {
        mPref.edit()
            .putBoolean(KEY, value!!)
            .apply()
    }




    fun getStringValue(KEY: String?): String? {
        return mPref.getString(KEY, "")
    }


    fun setString(KEY: String?, value: String?) {
        mPref.edit()
            .putString(KEY, value)
            .apply()
    }

    /**
     * Function to save auth token
     */


    fun remove(key: String?) {
        mPref.edit()
            .remove(key)
            .commit()
    }

    fun clear(): Boolean {
        return mPref.edit()
            .clear()
            .commit()
    }

    companion object {
        private const val PREF_NAME = "com.example.app.PREF_NAME"
        private var sInstance: PreferencesManager? = null
        @Synchronized
        fun initializeInstance(context: Context) {
            if (sInstance == null) {
                sInstance = PreferencesManager(context)
            }
        }

        @get:Synchronized
        val instance: PreferencesManager?
            get() {
                checkNotNull(sInstance) {
                    PreferencesManager::class.java.simpleName +
                            " is not initialized, call initializeInstance(..) method first."
                }
                return sInstance
            }
    }

}