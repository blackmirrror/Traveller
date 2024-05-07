package ru.blackmirrror.traveller.domain.repositories

import ru.blackmirrror.traveller.domain.models.Favorite
import ru.blackmirrror.traveller.domain.models.Mark
import ru.blackmirrror.traveller.domain.models.MarkLocal
import ru.blackmirrror.traveller.domain.models.ResultState
import ru.blackmirrror.traveller.domain.models.Subscribe
import ru.blackmirrror.traveller.domain.models.UserResponse

interface MarkRepository {
    suspend fun getAllMarks(): ResultState<List<Mark>>
    suspend fun createMark(mark: Mark): ResultState<Mark>
    suspend fun updateMark(mark: Mark): ResultState<Mark>
    suspend fun deleteMark(id: Long): ResultState<Unit>

    suspend fun getFavoriteMarksByUserId(): ResultState<List<Mark>>
    suspend fun addFavoriteMark(mark: Mark): ResultState<Favorite>
    suspend fun deleteFavoriteMark(userId: Long, markId: Long): ResultState<Unit>

    suspend fun getSubscribesByUserId(): ResultState<List<UserResponse>>
    suspend fun addSubscribe(subscribe: UserResponse): ResultState<Subscribe>
    suspend fun deleteSubscribe(id: Long): ResultState<Unit>

}