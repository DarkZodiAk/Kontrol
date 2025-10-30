package com.darkzodiak.kontrol.scheduling.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.darkzodiak.kontrol.scheduling.local.entity.Event

@Dao
interface EventDao {

    @Insert
    suspend fun addEvent(event: Event)

    @Query("DELETE FROM event WHERE id=:id")
    suspend fun deleteEventById(id: Long)

    @Query("SELECT * FROM event")
    suspend fun getAllEvents(): List<Event>
}