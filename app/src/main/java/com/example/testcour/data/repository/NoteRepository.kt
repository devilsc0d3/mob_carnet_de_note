package com.example.testcour.data.repository

import com.example.testcour.data.local.NoteDao
import com.example.testcour.data.local.NoteEntity
import kotlinx.coroutines.flow.Flow
import java.io.File

class NoteRepository(private val noteDao: NoteDao) {

    fun getNotes(): Flow<List<NoteEntity>> = noteDao.getAll()

    fun getNote(id: Long): Flow<NoteEntity?> = noteDao.getById(id)

    suspend fun addNote(note: NoteEntity): Long = noteDao.insert(note)

    suspend fun deleteNote(note: NoteEntity) {
        noteDao.delete(note)
        note.photoPath?.let { File(it).delete() }
    }
}
