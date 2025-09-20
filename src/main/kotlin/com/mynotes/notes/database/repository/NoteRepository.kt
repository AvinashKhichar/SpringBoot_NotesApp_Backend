package com.mynotes.notes.database.repository

import com.mynotes.notes.database.model.Note
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface NoteRepository: MongoRepository<Note, ObjectId> {
    fun findByOwnerId(ownerId : ObjectId): List<Note>
}