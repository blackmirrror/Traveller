package ru.blackmirrror.traveller.data.repositories

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask.TaskSnapshot
import com.google.firebase.storage.ktx.storage
import ru.blackmirrror.traveller.data.remote.ApiService
import ru.blackmirrror.traveller.domain.models.Favorite
import ru.blackmirrror.traveller.domain.models.Mark
import ru.blackmirrror.traveller.domain.models.MarkLocal
import ru.blackmirrror.traveller.domain.models.NoContent
import ru.blackmirrror.traveller.domain.models.NoInternet
import ru.blackmirrror.traveller.domain.models.OtherError
import ru.blackmirrror.traveller.domain.models.ResultState
import ru.blackmirrror.traveller.domain.models.ServerError
import ru.blackmirrror.traveller.domain.models.Subscribe
import ru.blackmirrror.traveller.domain.models.UserResponse
import ru.blackmirrror.traveller.domain.repositories.AuthRepository
import ru.blackmirrror.traveller.domain.repositories.MarkRepository
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class MarkRepositoryImpl(
    private val service: ApiService,
    private val authRepository: AuthRepository
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
            val user = authRepository.getCurrentUser()
            if (user != null) {
                mark.user = user
            }

            mark.imageUrl?.let { imagePath ->
                val file = Uri.fromFile(File(imagePath))
                val storageRef = Firebase.storage.reference
                val imagesRef = storageRef.child("images/${user?.id ?: "guest"}/${file.lastPathSegment}")
                val uploadTask = imagesRef.putFile(file)

                val taskSnapshot = suspendCoroutine<TaskSnapshot> { continuation ->
                    uploadTask.addOnSuccessListener { snapshot ->
                        continuation.resume(snapshot)
                    }.addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
                }

                mark.imageUrl = taskSnapshot.metadata?.path
            }

            ApiUtils.handleResponse(service.createMark(mark))
        } catch (e: Exception) {
            ResultState.Error(NoInternet)
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

    override suspend fun getFavoriteMarksByUserId(): ResultState<List<Mark>> {
        return try {
            val user = authRepository.getCurrentUser()
            if (user != null) {
                ApiUtils.handleResponse(service.getAllFavoriteMarksByUserId(user.id))
            }
            ResultState.Error(OtherError)
        } catch (e: Exception) {
            ResultState.Error(NoInternet)
        }
    }

    override suspend fun addFavoriteMark(mark: Mark): ResultState<Favorite> {
        return try {
            val user = authRepository.getCurrentUser()
            if (user != null) {
                ApiUtils.handleResponse(service.createFavorite(
                    Favorite(mark = mark, user = user)
                ))
            }
            ResultState.Error(OtherError)
        } catch (e: Exception) {
            ResultState.Error(NoInternet)
        }
    }

    override suspend fun deleteFavoriteMark(userId: Long, markId: Long): ResultState<Unit> {
        return try {
            val res = service.deleteFavorite(markId)
            if (res.isSuccessful)
                ResultState.Success(Unit)
            else
                ApiUtils.handleResponse(res)
        } catch (e: Exception) {
            return ResultState.Error(NoInternet)
        }
    }

    override suspend fun getSubscribesByUserId(): ResultState<List<UserResponse>> {
        return try {
            val user = authRepository.getCurrentUser()
            if (user != null) {
                ApiUtils.handleResponse(service.getAllSubscribesByUserId(user.id))
            }
            else {
                ResultState.Error(OtherError)
            }
        } catch (e: Exception) {
            ResultState.Error(NoInternet)
        }
    }

    override suspend fun addSubscribe(subscribe: UserResponse): ResultState<Subscribe> {
        return try {
            val user = authRepository.getCurrentUser()
            if (user != null) {
                ApiUtils.handleResponse(service.createSubscribe(
                    Subscribe(user = user, subscribe = subscribe)
                ))
            }
            ResultState.Error(OtherError)
        } catch (e: Exception) {
            ResultState.Error(NoInternet)
        }
    }

    override suspend fun deleteSubscribe(id: Long): ResultState<Unit> {
        TODO("Not yet implemented")
    }
}