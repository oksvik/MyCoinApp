package com.dudar.mycoinapp.model

import com.google.gson.annotations.SerializedName

data class ChartsResponse (val status :String,
                           val name : String,
                           val unit : String,
                           val period : String,
                           val description : String,
                           var values : MutableList<Value> = mutableListOf())

data class Value (@SerializedName("x") val timestamp : Long,
                  @SerializedName("y") val price: Int)

