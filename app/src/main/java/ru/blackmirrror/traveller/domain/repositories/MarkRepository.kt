package ru.blackmirrror.traveller.domain.repositories

import ru.blackmirrror.traveller.domain.models.Mark
import ru.blackmirrror.traveller.domain.models.ResultState

interface MarkRepository {
    suspend fun getAllMarks(): ResultState<List<Mark>>
    suspend fun createMark(mark: Mark): ResultState<Mark>
    suspend fun updateMark(mark: Mark): ResultState<Mark>
    suspend fun deleteMark(id: Long): ResultState<Unit>
}