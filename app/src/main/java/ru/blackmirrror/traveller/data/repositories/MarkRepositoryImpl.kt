package ru.blackmirrror.traveller.data.repositories

import okhttp3.MultipartBody
import ru.blackmirrror.traveller.data.local.UserSharedPreferences
import ru.blackmirrror.traveller.data.remote.ApiService
import ru.blackmirrror.traveller.domain.models.MarkResponse
import ru.blackmirrror.traveller.domain.models.NoInternet
import ru.blackmirrror.traveller.domain.models.OtherError
import ru.blackmirrror.traveller.domain.models.ResultState
import ru.blackmirrror.traveller.domain.models.ValidationError
import ru.blackmirrror.traveller.domain.repositories.MarkRepository

internal class MarkRepositoryImpl(
    private val service: ApiService,
    private val userPrefs: UserSharedPreferences
) : MarkRepository {
    override suspend fun getAllMarks(): ResultState<List<MarkResponse>> {
        return try {
            val res = service.getAllTags()
            if (res.isSuccessful) {
                val body = res.body()
                if (body != null)
                    ResultState.Success(body)
                else
                    ResultState.Error(ValidationError)
            }
            else {
                ResultState.Error(OtherError)
            }
        } catch (e: Exception) {
            ResultState.Error(NoInternet)
        }
    }

override suspend fun createMark(markResponse: MarkResponse): ResultState<MarkResponse> {
    val latitudePart =
        MultipartBody.Part.createFormData("latitude", markResponse.latitude.toString())
    val longitudePart =
        MultipartBody.Part.createFormData("longitude", markResponse.longitude.toString())
    val descriptionPart = MultipartBody.Part.createFormData("description", markResponse.description)

    return try {
        var token = userPrefs.currentToken
        if (token != null) token = "Bearer $token"
        val res = service.createTag(token, latitudePart, longitudePart, descriptionPart)
        if (res.isSuccessful) {
            val body = res.body()
            if (body != null)
                ResultState.Success(body)
            else
                ResultState.Error(ValidationError)
        }
        ResultState.Error(OtherError)
    } catch (e: Exception) {
        return ResultState.Error(NoInternet)
    }
}
}