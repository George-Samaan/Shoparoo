package com.example.shoparoo.ui.auth.view

fun validateEmail(email: String): Boolean {
    val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    return email.matches(emailPattern.toRegex())
}

fun validatePassword(password: String): Boolean {
    val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
    return password.matches(passwordPattern.toRegex())
}

fun validateName(name: String): Boolean {
    val namePattern = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$"
    return name.matches(namePattern.toRegex())
}
