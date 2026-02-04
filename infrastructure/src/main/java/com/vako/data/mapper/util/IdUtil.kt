package com.vako.data.mapper.util

import android.util.Base64
import java.security.MessageDigest
import java.text.Normalizer
import java.util.Locale

private val sha256 = MessageDigest.getInstance("SHA-256")

fun String.toStableId(): String {
    val normalized = Normalizer.normalize(this.trim().lowercase(Locale.ROOT), Normalizer.Form.NFKC)
    val digest = sha256.digest(normalized.toByteArray(Charsets.UTF_8))
    return Base64.encodeToString(digest, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
}