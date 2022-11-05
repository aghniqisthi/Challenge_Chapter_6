package com.example.challengechapter6.workers

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.challengechapter6.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

fun makeStatusNotification(message: String, context: Context) {
    // Create the notification
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(NOTIFICATION_TITLE)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVibrate(LongArray(0))

    // Show the notification
    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
}

fun blurBitmap(bitmap: Bitmap, applicationContext: Context): Bitmap {
    lateinit var rsContext: RenderScript
    try {
        // Create the output bitmap
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)

        // Blur the image
        rsContext = RenderScript.create(applicationContext, RenderScript.ContextType.DEBUG)
        val inAlloc = Allocation.createFromBitmap(rsContext, bitmap)
        val outAlloc = Allocation.createTyped(rsContext, inAlloc.type)
        val theIntrinsic = ScriptIntrinsicBlur.create(rsContext, Element.U8_4(rsContext))
        theIntrinsic.apply {
            setRadius(10f)
            theIntrinsic.setInput(inAlloc)
            theIntrinsic.forEach(outAlloc)
        }
        outAlloc.copyTo(output)

        return output
    } finally {
        rsContext.finish()
    }
}

private fun doBlurBitmap(rsContext: RenderScript,
                         scriptIntrinsicBlur: ScriptIntrinsicBlur,
                         bitmapToBlur: Bitmap): Bitmap {
    // Create the output bitmap that will hold the blurred image
    val blurredBitmap = bitmapToBlur.copy(bitmapToBlur.config, true)

    // Create Allocations for Renderscript to run
    val inAlloc = Allocation.createFromBitmap(rsContext, bitmapToBlur)
    val outAlloc = Allocation.createTyped(rsContext, inAlloc.type)

    scriptIntrinsicBlur.apply {
        // Set the Allocation input for the Blur script
        scriptIntrinsicBlur.setInput(inAlloc)
        // Execute the Blur process
        scriptIntrinsicBlur.forEach(outAlloc)
    }

    // Copy the result to the output bitmap
    outAlloc.copyTo(blurredBitmap)

    // Recycle the "bitmapToBlur" as we are returning a new one
    bitmapToBlur.recycle()

    // Release resources held by allocations only
    inAlloc.destroy()
    outAlloc.destroy()

    // Return the blurred bitmap
    return blurredBitmap
}

fun writeBitmapToFile(applicationContext: Context, bitmap: Bitmap): Uri {
    val name = String.format("blur-filter-output-%s.png", UUID.randomUUID().toString())
    val outputDir = File(applicationContext.filesDir, OUTPUT_PATH)
    if (!outputDir.exists()) {
        outputDir.mkdirs() // should succeed
    }
    val outputFile = File(outputDir, name)
    var out: FileOutputStream? = null
    try {
        out = FileOutputStream(outputFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 10 /* ignored for PNG */, out)
    }
    finally {
        out?.let {
            try {
                it.close()
            }
            catch (ignore: IOException) {

            }
        }
    }
    return Uri.fromFile(outputFile)
}

fun cleanUpTempFiles(applicationContext: Context) {
    // Output Directory where the temporary image files are present
    val outputDir = File(applicationContext.filesDir, OUTPUT_PATH)
    // Check if the Output Directory exists
    if (outputDir.exists()) {
        // When the Output Directory exists
        // Get all PNG files in the folder and delete them
        outputDir.listFiles()?.forEach { file: File? ->
            file?.takeIf { it.name.isNotEmpty() && it.name.endsWith(".png") }?.run {
                // Delete the temporary PNG file
                val isFileDeleted = delete()
                // Log the delete result
                Log.i(TAG, "Deleted $name - $isFileDeleted")
            }
        }
    }
}

fun saveImageToMedia(applicationContext: Context, bitmapToSaveUriStr: String?): String? {
    // Do not do anything if the Uri string is invalid or empty
    if (bitmapToSaveUriStr.isNullOrEmpty()) return null

    // Get the ContentResolver instance
    val contentResolver = applicationContext.contentResolver

    // Get the Bitmap to be saved from the given Uri
    val bitmapToSave = BitmapFactory.decodeStream(
        contentResolver.openInputStream(Uri.parse(bitmapToSaveUriStr))
    )

    // Get the Date Formatter
    val dateFormatter = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())

    // Write the Image file to MediaStore filesystem and return its Uri String
    return MediaStore.Images.Media.insertImage(
        contentResolver,
        bitmapToSave,
        TITLE_IMAGE,
        dateFormatter.format(Date())
    )
}



