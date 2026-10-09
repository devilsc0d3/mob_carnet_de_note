package com.example.testcour.data.photo

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/** Gère les photos des notes dans le stockage interne de l'application (files/photos). */
class PhotoStorage(private val context: Context) {

    private val photoDir: File
        get() = File(context.filesDir, "photos").apply { mkdirs() }

    /** Crée un fichier vide et renvoie son Uri FileProvider, à passer à l'appareil photo. */
    fun createPhotoFile(): Pair<File, Uri> {
        val file = File(photoDir, "photo_${UUID.randomUUID()}.jpg")
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        return file to uri
    }

    /** Copie une image choisie dans la galerie vers le stockage interne. */
    suspend fun importFromUri(source: Uri): File? = withContext(Dispatchers.IO) {
        runCatching {
            val file = File(photoDir, "photo_${UUID.randomUUID()}.jpg")
            context.contentResolver.openInputStream(source)!!.use { input ->
                file.outputStream().use { output -> input.copyTo(output) }
            }
            file
        }.getOrNull()
    }

    fun delete(path: String) {
        File(path).delete()
    }
}
