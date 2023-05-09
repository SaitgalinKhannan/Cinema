package com.khannan

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun main() {

    val a = Pair(19, 1)
    println(Json.encodeToString(a))
}
