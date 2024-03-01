package ru.blackmirrror.traveller.domain.repositories

import ru.blackmirrror.traveller.domain.models.MarkResponse
import ru.blackmirrror.traveller.domain.models.ResultState

interface MarkRepository {
    suspend fun getAllMarks(): ResultState<List<MarkResponse>>
    suspend fun createMark(markResponse: MarkResponse): ResultState<MarkResponse>
}