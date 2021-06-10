package com.joe.podcastplayer.extension

import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.Spannable
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.security.MessageDigest
import java.util.regex.Pattern

val String.isJson: Boolean
    get() {
        try {
            JSONObject(this)
        } catch (ex: JSONException) {
            try {
                JSONArray(this)
            } catch (ex1: JSONException) {
                return false
            }
        }
        return true
    }

val String.isEmail: Boolean
    get() {
        val expression = "^[A-Z0-9a-z._%+-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(this)
        return matcher.matches()
    }

val String.isLanguage: Boolean
    get() {
        val rule = Regex("[a-z]{2}[A-Z]{2}")
        return rule.matches(this)
    }

val String.isLanguageTag: Boolean
    get() {
        val rule = Regex("[a-z]{2}[-][A-Z]{2}")
        return rule.matches(this)
    }

val String.isContainAlphabet: Boolean
    get() = this.any { it in 'A'..'Z' || it in 'a'..'z' }

val String.md5: String
    get() {
        var md5: MessageDigest? = null
        try {
            md5 = MessageDigest.getInstance("MD5")
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }

        val charArray = this.toCharArray()
        val byteArray = ByteArray(charArray.size)
        for (i in charArray.indices) {
            byteArray[i] = charArray[i].toByte()
        }
        val md5Bytes = md5!!.digest(byteArray)
        val hexValue = StringBuffer()
        for (i in md5Bytes.indices) {
            val `val` = md5Bytes[i].toInt() and 0xff
            if (`val` < 16) {
                hexValue.append("0")
            }
            hexValue.append(Integer.toHexString(`val`))
        }
        return hexValue.toString().substring(8, 24)
    }

val String.uriEncode: String
    get() {
        var result = Uri.parse("").buildUpon().appendPath(this).build().toString()
        if (result.substring(0, 1) == "/") result = result.substring(1, result.length)
        return result
    }

val String.isVocabularyElements: Boolean
    get() {
        if (this.isEmpty()) return false
        return !this.toCharArray().any { !it.isVocabularyElement }
    }

val Char.isVocabularyElement: Boolean
    get() = (this in 'a'..'z' || this in 'A'..'Z') || (this == '\'') || (this == ' ') || (this == '-')

fun String.fromHtml(): Spannable {
    return when {
        isNOrHigher -> Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY) as Spannable
        else -> Html.fromHtml(this) as Spannable
    }
}