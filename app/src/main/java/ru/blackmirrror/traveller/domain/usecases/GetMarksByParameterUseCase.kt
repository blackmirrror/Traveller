package ru.blackmirrror.traveller.domain.usecases

import ru.blackmirrror.traveller.domain.models.MarkResponse
import ru.blackmirrror.traveller.domain.models.SortType

class GetMarksByParameterUseCase {

    operator fun invoke(
        sortType: SortType,
        word: String?,
        allMarks: List<MarkResponse>
    ): List<MarkResponse> {
        var result = allMarks
        if (word != null) {
            result = result.filter {
                word in it.description || (it.user?.username?.contains(word) == true) }
        }
        return when (sortType) {
            SortType.BY_AUTHOR -> result.sortedBy { it.user?.username }
            SortType.BY_COUNT_LIKES -> result.sortedByDescending { it.likes }
            else -> result
        }
    }
}