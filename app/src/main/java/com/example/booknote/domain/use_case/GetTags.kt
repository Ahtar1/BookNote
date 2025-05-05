package com.example.booknote.domain.use_case

import com.example.booknote.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetTags(
    private val repository: NoteRepository
) {

    suspend operator fun invoke(): Flow<List<String>> {
        return repository.getTags()
    }
}