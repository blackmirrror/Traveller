package ru.blackmirrror.traveller.domain.usecases

import ru.blackmirrror.traveller.domain.models.MarkLocal

class MapMarksAndFavoriteUseCase {
    operator fun invoke(marks: List<MarkLocal>, favorite: List<MarkLocal>): List<MarkLocal> {
        val result = mutableListOf<MarkLocal>()
        for (i in marks.indices) {
            if (favorite.find { it.id == marks[i].id } != null) {
                result.add(marks[i].copy(isFavorite = true))
            }
            else {
                result.add(marks[i])
            }
        }
        return result
    }
}