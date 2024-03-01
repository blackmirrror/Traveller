package ru.blackmirrror.traveller.features.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.blackmirrror.traveller.domain.models.EmptyFields
import ru.blackmirrror.traveller.domain.models.ErrorType
import ru.blackmirrror.traveller.domain.models.MarkResponse
import ru.blackmirrror.traveller.domain.models.OtherError
import ru.blackmirrror.traveller.domain.models.ResultState
import ru.blackmirrror.traveller.domain.models.ValidationError
import ru.blackmirrror.traveller.domain.usecases.CreateMarkUseCase
import ru.blackmirrror.traveller.domain.usecases.GetAllMarksUseCase
import ru.blackmirrror.traveller.domain.usecases.IsLoggingUserUseCase

class MapViewModel(
    private val getAllMarksUseCase: GetAllMarksUseCase,
    private val createMarkUseCase: CreateMarkUseCase,
    private val isLoggingUserUseCase: IsLoggingUserUseCase
) : ViewModel() {

    private val _isLoggingUser = MutableLiveData<Boolean>()
    val isLoggingUser: LiveData<Boolean> = _isLoggingUser

    private val _currentMarks = MutableLiveData<List<MarkResponse>?>()
    val currentMarks: LiveData<List<MarkResponse>?> = _currentMarks

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        initUser()
        fetchData()
    }

    private fun initUser() {
        viewModelScope.launch {
            _isLoggingUser.postValue(isLoggingUserUseCase.invoke())
        }
    }

    private fun fetchData() {
        viewModelScope.launch {
            _loading.postValue(true)
            when (val marks = getAllMarksUseCase.invoke()) {
                is ResultState.Success -> {
                    _currentMarks.postValue(marks.data)
                }

                is ResultState.Error -> {
                    when (marks.error) {
                        is ValidationError -> _error.postValue("Ошибка валидации")
                        is OtherError -> _error.postValue("Упс, с данными что-то не так")
                    }
                }

                else -> {}
            }
            _loading.postValue(false)
        }
    }

    fun createMark(description: String, latitude: String, longitude: String) {
        viewModelScope.launch {
            _loading.postValue(true)
            when (val mark = createMarkUseCase.invoke(description, latitude, longitude)) {
                is ResultState.Success -> {
                    _currentMarks.value = _currentMarks.value.orEmpty() + mark.data
                }

                is ResultState.Error -> {
                    when (mark.error) {
                        is EmptyFields -> _error.postValue("Проверьте, все ли поля заполнены")
                        is ErrorType -> _error.postValue("Проверьте правильность данных")
                        is ValidationError -> _error.postValue("Проверьте правильность данных")
                        is OtherError -> _error.postValue("Упс, с данными что-то не так")
                    }
                }

                else -> {}
            }
            _loading.postValue(false)
        }
    }
}