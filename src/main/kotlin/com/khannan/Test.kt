package com.khannan

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun main() {

    Pair(19, 1)
    val title = "Армагеддон"
    println(Json.encodeToString(title))
}
