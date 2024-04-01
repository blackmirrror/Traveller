package ru.blackmirrror.traveller.domain.usecases

import ru.blackmirrror.traveller.domain.models.Mark
import ru.blackmirrror.traveller.domain.models.ResultState
import ru.blackmirrror.traveller.domain.models.SortType
import ru.blackmirrror.traveller.domain.repositories.MarkRepository

class GetAllMarksUseCase(private val markRepository: MarkRepository) {
    suspend operator fun invoke(sort: SortType? = null): ResultState<List<Mark>> {
        return when (val result = markRepository.getAllMarks()) {
            is ResultState.Success -> {
                when (sort) {
                    SortType.BY_COUNT_LIKES -> ResultState.Success(result.data.sortedBy { it.likes })
                    SortType.BY_AUTHOR -> ResultState.Success(result.data.sortedBy { it.user?.username })
                    else -> result
                }
            }
            else -> {result}
        }
    }
}