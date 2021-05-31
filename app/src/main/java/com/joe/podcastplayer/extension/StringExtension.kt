package com.joe.podcastplayer.extension

import android.content.ClipData
import android.content.ClipboardManager
import android.net.Uri
import android.net.UrlQuerySanitizer
import android.os.Build
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap

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

val String.isDynamicLink: Boolean
    get() = contains("https://") && (contains(".app.goo.gl") || contains(".page.link"))

val String.isVocabularyElements: Boolean
    get() {
        if (this.isEmpty()) return false
        return !this.toCharArray().any { !it.isVocabularyElement }
    }

val Char.isVocabularyElement: Boolean
    get() = (this in 'a'..'z' || this in 'A'..'Z') || (this == '\'') || (this == ' ') || (this == '-')

// unicode for chinese document
// https://en.wikipedia.org/wiki/CJK_Unified_Ideographs_(Unicode_block)
val String.isChineseName: Boolean
    get() {
        val rule = Regex("[\\u4E00-\\u9FFF]{2,}")
        return rule.matches(this)
    }

fun String.wrapHtmlColor(@ColorInt color: Int): String {
    val originalText = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(this)
    }.toString()
    val hexColor = String.format("#%06X", 0xFFFFFF and color)
    return "<font color='$hexColor'>$originalText</font>"
}

fun String.getUrlParamsValue(key: String): String? {
    val queryMap = HashMap<String, String>()
    val sanitizer = UrlQuerySanitizer(this)
    val sanitizerParameterList = sanitizer.parameterList
    for (valuePair in sanitizerParameterList) {
        queryMap[valuePair.mParameter] = valuePair.mValue
        if (valuePair.mParameter.contains(key)) return valuePair.mValue
    }
    return null
}

@Throws(UnsupportedEncodingException::class)
fun String.getQueryMapFromUrlLink(): HashMap<String, String> {
    val uri = Uri.parse(this)
    val queryMap = HashMap<String, String>()
    val sanitizer = UrlQuerySanitizer(this)
    sanitizer.unregisteredParameterValueSanitizer = UrlQuerySanitizer.getAllButNulLegal()
    sanitizer.parseUrl(this)
    val sanitizerParameterList = sanitizer.parameterList
    for (valuePair in sanitizerParameterList) queryMap[valuePair.mParameter] = valuePair.mValue
    return queryMap
}

fun String.processReferrerLink(isGoogleUacProcessEnabled: Boolean = false): String {
    val uri = Uri.parse(this)
    var scheme = uri.scheme
    var server: String? = null
    var path: String? = null
    if (uri.authority == null) {
        val fixedUri = Uri.parse("${scheme ?: "https"}://${uri.path ?: ""}")
        server = fixedUri.authority
        if (server != null) {
            scheme = "https"
            path = fixedUri.path
        }
    } else {
        scheme = scheme ?: "unknown"
        server = uri.authority
        path = uri.path
    }
    scheme = "https"
    val queries = this.getQueryMapFromUrlLink()
    val isUac = queries.containsKey("campaignid") &&
        queries.containsKey("network") &&
        queries.containsKey("conv") &&
        queries.containsKey("adsplayload") &&
        queries.containsKey("gclid")
    if (isUac && isGoogleUacProcessEnabled) {
        val language = Locale.getDefault().language
        val systemLanguage = when {
            language.contains("zh") -> "tw"
            language.contains("ja") -> "ja"
            language.contains("vi") -> "vn"
            else -> "en"
        }
        queries["utm_source"] = "uac_$systemLanguage"
        queries["utm_medium"] = queries["campaignid"]!!
        queries["utm_campaign"] = queries["network"]!!
    }
    val finalReferer: String = Uri.parse("$scheme://$server$path").buildUpon().apply { queries.forEach { appendQueryParameter(it.key, it.value) } }.build().toString()
    return finalReferer
}

fun String.fromHtml(): Spannable {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY) as Spannable
        else -> Html.fromHtml(this) as Spannable
    }
}