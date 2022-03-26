package com.humoyunbek.weatherlocation

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.humoyunbek.weatherlocation.model.Data
import com.humoyunbek.weatherlocation.retrofit.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel:ViewModel() {
    var liveData  =MutableLiveData<Data>()
    val appid = "acea7a4cbba529d6a0ca720d5ac2dbf8"

    fun getData(lat:String,lon:String):MutableLiveData<Data>{
        ApiClient.apiService.getData(lat,lon,"metric",appid).enqueue(object :Callback<Data>{
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                if (response.isSuccessful){
                    liveData.value = response.body()
                    Log.d(TAG, "onResponse: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t.message}")
            }
        })
        return liveData
    }
}