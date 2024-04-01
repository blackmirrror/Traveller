package ru.blackmirrror.traveller.features.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.blackmirrror.traveller.domain.models.Conflict
import ru.blackmirrror.traveller.domain.models.EmptyFields
import ru.blackmirrror.traveller.domain.models.ErrorType
import ru.blackmirrror.traveller.domain.models.Mark
import ru.blackmirrror.traveller.domain.models.NoContent
import ru.blackmirrror.traveller.domain.models.NoInternet
import ru.blackmirrror.traveller.domain.models.NotFound
import ru.blackmirrror.traveller.domain.models.OtherError
import ru.blackmirrror.traveller.domain.models.ResultState
import ru.blackmirrror.traveller.domain.models.ServerError
import ru.blackmirrror.traveller.domain.models.SortType
import ru.blackmirrror.traveller.domain.models.UserResponse
import ru.blackmirrror.traveller.domain.usecases.CreateMarkUseCase
import ru.blackmirrror.traveller.domain.usecases.GetAllMarksUseCase
import ru.blackmirrror.traveller.domain.usecases.GetMarksByParameterUseCase
import ru.blackmirrror.traveller.domain.usecases.IsGuestUseCase
import ru.blackmirrror.traveller.domain.usecases.IsLoginUserUseCase

class MapViewModel(
    private val getAllMarksUseCase: GetAllMarksUseCase,
    private val createMarkUseCase: CreateMarkUseCase,
    private val isGuestUseCase: IsGuestUseCase,
    private val isLoginUserUseCase: IsLoginUserUseCase,
    private val getMarksByParameterUseCase: GetMarksByParameterUseCase
) : ViewModel() {

    private val _allMarks = MutableLiveData<List<Mark>?>()

    private val _isLoggingUser = MutableLiveData<Boolean>()
    val isLoggingUser: LiveData<Boolean> = _isLoggingUser

    private val _isCurrentUser = MutableLiveData<UserResponse>()
    val isCurrentUser: LiveData<UserResponse> = _isCurrentUser

    private val _currentMarks = MutableLiveData<List<Mark>?>()
    val currentMarks: LiveData<List<Mark>?> = _currentMarks

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
            if (isGuestUseCase.invoke())
                _isLoggingUser.postValue(true)
            else {
                val res = isLoginUserUseCase.invoke()
                if (res is ResultState.Success) {
                    _isLoggingUser.postValue(true)
                }
                else {
                    if (res.data != null) {
                        _error.postValue("Нет соединения, войду локально")
                        _isLoggingUser.postValue(true)
                    }
                    else {
                        _isLoggingUser.postValue(false)
                        _error.postValue("Нет соединения, не могу войти")
                    }
                }
            }
        }
    }

    private fun fetchData() {
        viewModelScope.launch {
            _loading.postValue(true)
            when (val marks = getAllMarksUseCase.invoke()) {
                is ResultState.Success -> {
                    _allMarks.postValue(marks.data)
                    _currentMarks.postValue(marks.data)
                }
                else -> handleError(marks)
            }
            _loading.postValue(false)
        }
    }

    fun getMarks(sortType: SortType, word: String? = null) {
        _currentMarks.postValue(
            _allMarks.value?.let { getMarksByParameterUseCase.invoke(sortType, word, it) }
        )
    }

    fun createMark(description: String, latitude: String, longitude: String, image: String?) {
        viewModelScope.launch {
            _loading.postValue(true)
            when (val mark = createMarkUseCase.invoke(description, latitude, longitude, image)) {
                is ResultState.Success -> {
                    _currentMarks.value = _currentMarks.value.orEmpty() + mark.data
                    _allMarks.value = _allMarks.value.orEmpty() + mark.data
                    getMarks(sortType = SortType.NONE)
                }
                else -> handleError(mark)
            }
            _loading.postValue(false)
        }
    }

    fun getMarkById(markId: Long): Mark? {
        return _currentMarks.value?.find { it.id == markId }
    }

    private fun <T> handleError(result: ResultState<T>) {
        if (result is ResultState.Error) {
            when (result.error) {
                is EmptyFields -> _error.postValue("Проверьте, все ли поля заполнены")
                is ErrorType -> _error.postValue("Проверьте правильность данных")
                is NoInternet -> _error.postValue("Соединение потеряно, работаю локально")
                is NoContent -> _error.postValue("Пока здесь пусто")
                is NotFound -> _error.postValue("Не найдено")
                is Conflict -> _error.postValue("Вы не можете подписаться на себя")
                is OtherError -> _error.postValue("Упс, с данными что-то не так")
                is ServerError -> _error.postValue("Неполадки на сервере, уже работаем над этим")
            }
        }
    }
}