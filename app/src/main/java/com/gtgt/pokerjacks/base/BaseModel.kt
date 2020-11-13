package com.gtgt.pokerjacks.base

import com.google.gson.JsonElement

open class BaseModel {
   val description: String = ""
   val errorCode: Int = 0
   var success: Boolean = false
}

data class AnyModel(val info: JsonElement) : BaseModel()