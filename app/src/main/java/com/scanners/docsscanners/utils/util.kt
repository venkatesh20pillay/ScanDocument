package com.scanners.docsscanners.utils
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.android.datatransport.runtime.Destination
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun getFileSize(context: Context, fileName: String): String {
    val file = File(context.filesDir, fileName)
    val fileSizeBytes: Long = file.length()
    val fileSizeKB: Long = fileSizeBytes/1024
    return if(fileSizeKB > 1024) {
        val fileSizeMB: Long = fileSizeKB/1024
        "$fileSizeMB MB"
    } else {
        "$fileSizeKB KB"
    }
}

@SuppressLint("Recycle")
fun copyPdfFileToAppDirectory(context: Context, pdfUri: Uri, destinationFileName: String) {
    val inputStream: InputStream? = context.contentResolver.openInputStream(pdfUri)
    val outputFile = File(context.filesDir, destinationFileName)
    FileOutputStream(outputFile).use { it
        inputStream?.copyTo(it)
    }
}

fun deleteFile(context: Context, fileName: String): Boolean {
    val file = File(context.filesDir, fileName)
    return file.deleteRecursively()
}

fun getFileUri(context: Context, fileName: String): Uri {
    val file = File(context.filesDir, fileName)
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}

fun renameFile(context: Context, oldFileName: String, newFileName: String) {
    val oldFile = File(context.filesDir, oldFileName)
    val newFileName = File(context.filesDir, newFileName)
    oldFile.renameTo(newFileName)
}