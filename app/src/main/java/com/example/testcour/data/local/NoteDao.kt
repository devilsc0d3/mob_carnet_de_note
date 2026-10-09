package com.example.testcour.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY date DESC")
    fun getAll(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    fun getById(id: Long): Flow<NoteEntity?>

    @Insert
    suspend fun insert(note: NoteEntity): Long

    @Delete
    suspend fun delete(note: NoteEntity)
}
