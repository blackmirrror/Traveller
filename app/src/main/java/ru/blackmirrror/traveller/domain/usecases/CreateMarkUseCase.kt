package ru.blackmirrror.traveller.domain.usecases

import ru.blackmirrror.traveller.domain.models.EmptyFields
import ru.blackmirrror.traveller.domain.models.ErrorType
import ru.blackmirrror.traveller.domain.models.Mark
import ru.blackmirrror.traveller.domain.models.ResultState
import ru.blackmirrror.traveller.domain.repositories.MarkRepository

class CreateMarkUseCase(private val markRepository: MarkRepository) {
    suspend operator fun invoke(
        description: String,
        latitude: String,
        longitude: String,
        imageUrl: String?
    ): ResultState<Mark> {
        if (description.isEmpty())
            return ResultState.Error(EmptyFields)
        if (!(isDouble(latitude) || isDouble(longitude)))
            return ResultState.Error(ErrorType)
        return markRepository.createMark(
            Mark(
                description = description,
                latitude = latitude.toDouble(),
                longitude = longitude.toDouble(),
                imageUrl = imageUrl
            )
        )
    }

    private fun isDouble(str: String): Boolean {
        return try {
            str.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }
}