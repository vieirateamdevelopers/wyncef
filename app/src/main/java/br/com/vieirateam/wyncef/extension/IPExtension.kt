package br.com.vieirateam.wyncef.extension

import androidx.appcompat.app.AppCompatActivity
import java.util.regex.Pattern

const val IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$"

fun AppCompatActivity.validateIPAddress(text : String) : Boolean {
    val pattern = Pattern.compile(IPADDRESS_PATTERN)
    val matcher = pattern.matcher(text)
    return matcher.matches()
}