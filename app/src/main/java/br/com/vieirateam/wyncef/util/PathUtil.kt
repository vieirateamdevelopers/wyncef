package br.com.vieirateam.wyncef.util

import android.net.Uri
import android.provider.DocumentsContract

object PathUtil {

    fun getPath(uri: Uri): String? {
        return try {
            var id = DocumentsContract.getDocumentId(uri)
            id = id.split(":")[1]
            return "/storage/emulated/0/$id"
        } catch (exception: RuntimeException) {
            null
        }
    }
}