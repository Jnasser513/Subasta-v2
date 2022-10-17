package com.project.itexpdf

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class ScreenShot (private val mContext: Context){

    fun getViewScreenshot(view: View, height: Int, width: Int): Bitmap? {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable: Drawable = view.background
        bgDrawable.draw(canvas)
        view.draw(canvas)
        return bitmap
    }

    fun saveScreenshot(bitmap: Bitmap) {

        val filename    = "Screenshot.jpg"
        val defaultFile = File(mContext.getExternalFilesDir(null)!!.absolutePath + "/Screenshot")

        if (!defaultFile.exists()) defaultFile.mkdirs()
        var file = File(defaultFile, filename)
        if (file.exists()) {
            file.delete()
            file = File(defaultFile, filename)
        }
        val fos: FileOutputStream
        try {
            fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: FileNotFoundException) {
        } catch (e: IOException) {
        }

    }
}