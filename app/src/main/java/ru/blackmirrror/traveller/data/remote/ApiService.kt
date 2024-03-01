package ru.blackmirrror.traveller.data.remote

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import ru.blackmirrror.traveller.domain.models.LoginResponse
import ru.blackmirrror.traveller.domain.models.MarkResponse
import ru.blackmirrror.traveller.domain.models.ResultState
import ru.blackmirrror.traveller.domain.models.UserRequest
import ru.blackmirrror.traveller.domain.models.UserResponse

interface ApiService {

    @GET("api/tags")
    suspend fun getAllTags(): Response<List<MarkResponse>>

    @Multipart
    @POST("api/tags")
    suspend fun createTag(
        @Header("Authorization") token: String?,
        @Part latitude: MultipartBody.Part,
        @Part longitude: MultipartBody.Part,
        @Part description: MultipartBody.Part
    ): Response<MarkResponse>

    @POST("api/auth/register")
    suspend fun registerUser(userRequest: UserRequest): Response<UserResponse>

    @Multipart
    @POST("api/auth/jwt/login")
    suspend fun loginUser(
        @Part username: MultipartBody.Part,
        @Part password: MultipartBody.Part
    ): Response<LoginResponse>
}