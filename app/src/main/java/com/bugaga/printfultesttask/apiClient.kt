package com.bugaga.printfultesttask

import android.content.Context
import android.icu.util.Output
import android.os.Handler
import android.os.Message
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class apiClient(mainContext: Context, handler: Handler,
                private var dataList: MutableList<CountriesData>? = null) {
    val context = mainContext
    private val myHandler = handler
    private data class сountriesList(val Countries: List<CountriesData>)
    val queue: RequestQueue = Volley.newRequestQueue(context)


    fun getSummery(){
        val url = "https://api.covid19api.com/summary"

        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                parseResponseSummery(response)
            },
            {
                output("Something wrong with GET request" )
                sendMessage(0)
            })

        queue.add(stringRequest)
    }

    private fun parseResponseSummery(data: String) {
        val gson = GsonBuilder().create()
        val list = gson.fromJson(data,сountriesList::class.java)
        if (list == null || list.Countries.isEmpty())
            sendMessage(0)
        else {
            dataList?.clear()
            dataList?.addAll(list.Countries)
            sendMessage(1)
        }
    }

    fun sendMessage(data : Int){
        myHandler.sendMessage(Message.obtain(myHandler, data))
    }
    fun output(data: String){
        Log.w("myDebug",data)
    }
}