package com.example.testcour.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    /** Chemin absolu de la photo dans le stockage interne de l'app (null si pas de photo). */
    val photoPath: String?,
    /** Date de création en millisecondes (epoch). */
    val date: Long,
    val latitude: Double?,
    val longitude: Double?
) {
    val hasLocation: Boolean get() = latitude != null && longitude != null
}
