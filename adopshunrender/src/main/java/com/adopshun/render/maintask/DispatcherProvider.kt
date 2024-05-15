package com.adopshun.render.maintask

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Created By Matrix Marketers
 */
interface DispatcherProvider {
    fun main(): CoroutineDispatcher
    fun io(): CoroutineDispatcher
    fun default(): CoroutineDispatcher
    fun unconfined(): CoroutineDispatcher
}
