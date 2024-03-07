package ru.blackmirrror.traveller.features.utils

import ru.blackmirrror.traveller.domain.models.UserResponse
import java.util.Locale

class TextFormatter {
    companion object {

        private fun truncateFloat(value: Double): Double {
            return String.format(Locale.US,"%.6f", value).toDouble()
        }

        fun coordinatesToText(latitude: Double, longitude: Double): String {
            return "${truncateFloat(latitude)} ${truncateFloat(longitude)}"
        }

        fun likesAndAuthorToText(likes: Int, author: UserResponse?): String {
            return "$likes людям понравилось, от ${author?.username ?: "гостя"}"
        }
    }
}