package ru.blackmirrror.traveller.domain.models

data object EmptyFields: Exception()
data object ErrorType: Exception()

data object NoContent: Exception()
data object NotFound: Exception()
data object Conflict: Exception()
data object OtherError: Exception()
data object ServerError: Exception()