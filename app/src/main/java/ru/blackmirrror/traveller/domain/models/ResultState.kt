package ru.blackmirrror.traveller.domain.models

import java.lang.Exception

sealed class ResultState<T>(open val data: T? = null) {
    class Loading<T>(data: T? = null): ResultState<T>(data)
    class Success<T>(override val data: T): ResultState<T>(data)
    class Error<T>(val error: Exception, data: T? = null): ResultState<T>(data)
}