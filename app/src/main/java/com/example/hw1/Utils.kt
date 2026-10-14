package com.example.hw1

import kotlin.random.Random

fun generatePassword(minLength: Int = 10): String {
    val latinLower = "abcdefghijklmnopqrstuvwxyz"
    val latinUpper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val digits = "0123456789"
    val all = latinLower + latinUpper + digits

    val result = StringBuilder()
    result.append(latinLower.random())
    result.append(latinUpper.random())
    result.append(digits.random())

    val targetLength = maxOf(minLength, Random.nextInt(minLength, minLength + 5))
    while (result.length < targetLength) {
        result.append(all.random())
    }

    val chars = result.toString().toMutableList()
    chars.shuffle()
    return chars.joinToString("")
}

fun isValidEmail(email: String): Boolean {
    val regex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    return regex.matches(email)
}

fun isValidPassword(password: String): Boolean {
    if (password.length < 8) return false
    val hasLatin = password.any { it in 'a'..'z' || it in 'A'..'Z' }
    val hasDigit = password.any { it in '0'..'9' }
    return hasLatin && hasDigit
}