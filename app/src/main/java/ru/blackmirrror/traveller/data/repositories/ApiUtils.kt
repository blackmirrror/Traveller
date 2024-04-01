package ru.blackmirrror.traveller.data.repositories

import retrofit2.Response
import ru.blackmirrror.traveller.domain.models.Conflict
import ru.blackmirrror.traveller.domain.models.NoContent
import ru.blackmirrror.traveller.domain.models.NotFound
import ru.blackmirrror.traveller.domain.models.OtherError
import ru.blackmirrror.traveller.domain.models.ResultState
import ru.blackmirrror.traveller.domain.models.ServerError

object ApiUtils {
    fun <T> handleResponse(response: Response<T>): ResultState<T> {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null)
                ResultState.Success(body)
            else
                ResultState.Error(NoContent)
        } else if (response.code() == 404)
            return ResultState.Error(NotFound)
        else if (response.code() == 409)
            return ResultState.Error(Conflict)
        else if (response.code() >= 500)
            ResultState.Error(ServerError)
        else
            ResultState.Error(OtherError)
    }
}