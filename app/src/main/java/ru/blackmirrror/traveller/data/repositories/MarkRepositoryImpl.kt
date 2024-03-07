package ru.blackmirrror.traveller.data.repositories

import android.util.Log
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import ru.blackmirrror.traveller.data.local.UserSharedPreferences
import ru.blackmirrror.traveller.data.remote.ApiService
import ru.blackmirrror.traveller.domain.models.MarkResponse
import ru.blackmirrror.traveller.domain.models.NoInternet
import ru.blackmirrror.traveller.domain.models.OtherError
import ru.blackmirrror.traveller.domain.models.ResultState
import ru.blackmirrror.traveller.domain.models.ValidationError
import ru.blackmirrror.traveller.domain.repositories.MarkRepository
import java.io.File

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
    //val imagePart = markResponse.image?.let { MultipartBody.Part.createFormData("image", it) }

    val file = markResponse.image?.let { File(it) }
    val requestFile = file?.let { RequestBody.create("multipart/form-data".toMediaTypeOrNull(), it) }
    val imagePart = requestFile?.let { MultipartBody.Part.createFormData("image", file.name, it) }

    return try {
        var token = userPrefs.currentToken
        token = if (token != null) "Bearer $token" else "null"
        Log.d("j", "createMark: $token")
        val res = service.createTag(token, latitudePart, longitudePart, descriptionPart, imagePart)
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