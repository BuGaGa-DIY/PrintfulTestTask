package com.bugaga.printfultesttask

import java.io.Serializable

data class CountriesData(val Country:String,val CountryCode:String,val TotalConfirmed:Int,val TotalDeaths:Int, val TotalRecovered:Int): Serializable
