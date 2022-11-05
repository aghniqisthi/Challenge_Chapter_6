package com.example.challengechapter6.testing

class Register {
    fun validateRegistrationInput(username: String, name:String, password: String, confirmedPassword: String, age:Int, address:String): Boolean {
        if (username.isEmpty() || name.isEmpty() || password.isEmpty() || confirmedPassword.isEmpty() || age.equals(0) || address.isEmpty()){
            return false
        }
        if (password != confirmedPassword){
            return false
        }
        if (password.count { it.isDigit() } < 6){
            return false
        }
        if (password.count { it.isDigit() } > 30){
            return false
        }
        if(!password.contains("_") || !password.contains(".") || !password.contains("*")){
            return false
        }
        if(!password.contains("1") ||
            !password.contains("2") ||
            !password.contains("3") ||
            !password.contains("4") ||
            !password.contains("5") ||
            !password.contains("6") ||
            !password.contains("7") ||
            !password.contains("8") ||
            !password.contains("9") ||
            !password.contains("0")){
            return false
        }
        return true
    }
}