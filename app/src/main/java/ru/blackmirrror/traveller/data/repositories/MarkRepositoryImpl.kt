package ru.blackmirrror.traveller.data.repositories

import ru.blackmirrror.traveller.data.remote.ApiService
import ru.blackmirrror.traveller.domain.models.Mark
import ru.blackmirrror.traveller.domain.models.NoContent
import ru.blackmirrror.traveller.domain.models.NoInternet
import ru.blackmirrror.traveller.domain.models.OtherError
import ru.blackmirrror.traveller.domain.models.ResultState
import ru.blackmirrror.traveller.domain.models.ServerError
import ru.blackmirrror.traveller.domain.repositories.MarkRepository

internal class MarkRepositoryImpl(
    private val service: ApiService
) : MarkRepository {
    override suspend fun getAllMarks(): ResultState<List<Mark>> {
        return try {
            ApiUtils.handleResponse(service.getAllMarks())
        } catch (e: Exception) {
            ResultState.Error(NoInternet)
        }
    }

    override suspend fun createMark(mark: Mark): ResultState<Mark> {
        return try {
            ApiUtils.handleResponse(service.createMark(mark))
        } catch (e: Exception) {
            return ResultState.Error(NoInternet)
        }
    }

    override suspend fun updateMark(mark: Mark): ResultState<Mark> {
        return try {
            if (mark.id != null) {
                ApiUtils.handleResponse(service.updateMark(mark.id!!, mark))
            }
            ResultState.Error(OtherError)
        } catch (e: Exception) {
            return ResultState.Error(NoInternet)
        }
    }

    override suspend fun deleteMark(id: Long): ResultState<Unit> {
        return try {
            val res = service.deleteMark(id)
            if (res.isSuccessful)
                ResultState.Success(Unit)
            else
                ApiUtils.handleResponse(res)
        } catch (e: Exception) {
            return ResultState.Error(NoInternet)
        }
    }
}