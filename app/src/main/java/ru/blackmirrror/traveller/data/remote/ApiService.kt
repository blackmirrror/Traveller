package ru.blackmirrror.traveller.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import ru.blackmirrror.traveller.domain.models.Favorite
import ru.blackmirrror.traveller.domain.models.Mark
import ru.blackmirrror.traveller.domain.models.Subscribe
import ru.blackmirrror.traveller.domain.models.UserRequest
import ru.blackmirrror.traveller.domain.models.UserResponse

interface ApiService {

    @POST("users/register")
    suspend fun registerUser(@Body userRequest: UserRequest): Response<UserResponse>

    @POST("users/login")
    suspend fun loginUser(@Body userRequest: UserRequest): Response<UserResponse>

    @GET("marks")
    suspend fun getAllMarks(): Response<List<Mark>>

    @POST("marks")
    suspend fun createMark(@Body mark: Mark): Response<Mark>

    @PUT("marks/{id}")
    suspend fun updateMark(@Path("id") id: Long, @Body mark: Mark): Response<Mark>

    @DELETE("marks/{id}")
    suspend fun deleteMark(@Path("id") id: Long): Response<Unit>

    @GET("favorite/{id}")
    suspend fun getAllFavoriteMarksByUserId(@Path("id") id: Long): Response<List<Mark>>

    @POST("favorite")
    suspend fun createFavorite(@Body favorite: Favorite): Response<Favorite>

    @DELETE("favorite/{id}")
    suspend fun deleteFavorite(@Path("id") id: Long): Response<Unit>

    @GET("subscribes/{id}")
    suspend fun getAllSubscribesByUserId(@Path("id") id: Long): Response<List<UserResponse>>

    @POST("subscribes")
    suspend fun createSubscribe(@Body subscribe: Subscribe): Response<Subscribe>

    @DELETE("subscribes/{id}")
    suspend fun deleteSubscribe(@Path("id") id: Long): Response<Unit>
}