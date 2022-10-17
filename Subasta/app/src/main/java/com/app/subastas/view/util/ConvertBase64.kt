package com.app.subastas.view.util

import android.content.ContentResolver
import android.net.Uri
import android.util.Base64
import java.io.IOException

class ConvertBase64 {

    fun uriToBase64(uri: Uri, resolver: ContentResolver): String {
        var encodedBase64: String = ""
        try {
            val bytes: ByteArray = readBytes(uri, resolver)
            encodedBase64 = Base64.encodeToString(bytes, 0)
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        return encodedBase64
    }

    @Throws(IOException::class)
    private fun readBytes(uri: Uri, resolver: ContentResolver): ByteArray {
        // this dynamically extends to take the bytes you read
        return resolver.openInputStream(uri)?.readBytes() ?: byteArrayOf()
    }

}