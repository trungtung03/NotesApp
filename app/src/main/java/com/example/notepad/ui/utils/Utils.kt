package com.example.quanlychitieu.utils

import java.text.SimpleDateFormat

object Utils {
    fun isValidDate(dateString: String?): Boolean {
        if (dateString == null) return false
        val format = SimpleDateFormat("dd/MM/yyyy")
        format.isLenient = false
        return try {
            format.parse(dateString)
            true
        } catch (e: Exception) {
            false
        }
    }
    fun getNumbersFromStringAndAndJoinNumbersToInteger(text: String?, separator: String = ","): Int {
        if (text.isNullOrEmpty() ) return 0
        val regex = Regex("\\d+")
        val numbers = regex.findAll(text)
            .map { it.value.toInt() }
            .toList()
        val joinedString = numbers.joinToString(separator = "")
        return joinedString.toInt()
    }

}
