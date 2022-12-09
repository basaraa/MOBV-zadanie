package com.example.zadaniemobv.others

open class LiveDataEvents<out T>(private val content: T) {
    private var isAlreadyUsed = false
    //zisťuje a zamädzuje opetovnému používaniu contentu
    fun getContentIfNotHandled(): T? {
        if (isAlreadyUsed) {
            return null
        } else {
            isAlreadyUsed = true
            return content
        }
    }
}