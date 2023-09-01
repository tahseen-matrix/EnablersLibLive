package com.adopshun.creator.retrofit

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*


class MainViewModel constructor(private val service: RetrofitService) : ViewModel() {

    val errorMessage = SingleLiveEvent<String>()
    var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val loading = SingleLiveEvent<Boolean>()

    val screenModel = SingleLiveEvent<String>()



   /* fun sendImage(user_id: RequestBody,unique_id: RequestBody, meta_data: RequestBody, session_id: RequestBody,file: MultipartBody.Part) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            loading.postValue(true)
            val response = service.sendScreenshot(user_id,unique_id,meta_data,session_id,file)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    screenModel.postValue(response.body())
                    loading.value = false
                } else {
                    val data = response.errorBody()!!.string()
                    onError(data)
                }
            }
        }
    }*/


    private fun onError(message: String) {
        errorMessage.postValue(message)
        loading.postValue(false)
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }




}