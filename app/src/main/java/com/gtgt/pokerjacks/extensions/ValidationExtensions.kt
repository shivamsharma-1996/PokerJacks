package com.gtgt.pokerjacks.extensions

import java.util.regex.Pattern

fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPhoneNumber(): Boolean {
    return (android.util.Patterns.PHONE.matcher(
        this
    ).matches() && this.length == 10 && this.startsWith("6") || this.startsWith("7") ||
            this.startsWith("8") || this.startsWith("9"))
}

fun String.isCountryCodeAttatched(): Boolean {
    return this.startsWith("+91")
}

fun String.getFormattedMobileNumber(): String {
    return this.removePrefix("+91")
}

fun String.isValidPassword(): Boolean {
    return Pattern.matches("((?=.*[a-z])(?=.*\\d)(?=.*[@#$%!]).{6,20})", this)
}

fun String.isValidIFSCCode(): Boolean {
    return Pattern.matches("^[A-Za-z]{4}0[A-Z0-9a-z]{6}\$", this)
}

fun String.isValidUPI(): Boolean {
    // [a-zA-Z0-9.\-_]{2,256}@[a-zA-Z]{2,64}
    return Pattern.matches("[a-zA-Z0-9.\\-_]{2,256}@[a-zA-Z]{2,64}", this)
}