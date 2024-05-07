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
import ru.blackmirrror.traveller.domain.models.MarkLocal
import ru.blackmirrror.traveller.domain.models.NoContent
import ru.blackmirrror.traveller.domain.models.NoInternet
import ru.blackmirrror.traveller.domain.models.NotFound
import ru.blackmirrror.traveller.domain.models.OtherError
import ru.blackmirrror.traveller.domain.models.ResultState
import ru.blackmirrror.traveller.domain.models.ServerError
import ru.blackmirrror.traveller.domain.models.SortType
import ru.blackmirrror.traveller.domain.models.UserResponse
import ru.blackmirrror.traveller.domain.repositories.AuthRepository
import ru.blackmirrror.traveller.domain.repositories.MarkRepository
import ru.blackmirrror.traveller.domain.usecases.CreateMarkUseCase
import ru.blackmirrror.traveller.domain.usecases.GetMarksByParameterUseCase
import ru.blackmirrror.traveller.domain.usecases.MapMarksAndFavoriteUseCase

class MapViewModel(
    private val createMarkUseCase: CreateMarkUseCase,
    private val getMarksByParameterUseCase: GetMarksByParameterUseCase,
    private val mapMarksAndFavoriteUseCase: MapMarksAndFavoriteUseCase,
    private val authRepository: AuthRepository,
    private val markRepository: MarkRepository
) : ViewModel() {

    private val _allMarks = MutableLiveData<List<MarkLocal>?>()

    private val _currentMarks = MutableLiveData<List<MarkLocal>?>()
    val currentMarks: LiveData<List<MarkLocal>?> = _currentMarks

    private val _favoriteMarks = MutableLiveData<List<MarkLocal>?>()
    val favoriteMarks: LiveData<List<MarkLocal>?> = _favoriteMarks

    private val _isLoggingUser = MutableLiveData<Boolean>()
    val isLoggingUser: LiveData<Boolean> = _isLoggingUser

    private val _isCurrentUser = MutableLiveData<UserResponse>()
    val isCurrentUser: LiveData<UserResponse> = _isCurrentUser

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
            if (authRepository.isGuest())
                _isLoggingUser.postValue(true)
            else {
                val res = authRepository.isLoginUser()
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
                _isCurrentUser.postValue(authRepository.getCurrentUser())
            }
        }
    }

    private fun fetchData() {
        viewModelScope.launch {
            _loading.postValue(true)
            when (val marks = markRepository.getAllMarks()) {
                is ResultState.Success -> {
                    mapWithFavorite(marks.data)
                }
                else -> handleError(marks)
            }
            _loading.postValue(false)
        }
    }

    private fun mapWithFavorite(marks: List<Mark>) {
        viewModelScope.launch {
            when (val favorite = markRepository.getFavoriteMarksByUserId()) {
                is ResultState.Success -> {
                    val new = mapMarksAndFavoriteUseCase(
                        marks = marks.map { it.toLocal() },
                        favorite = favorite.data.map { it.toLocal() }
                    )
                    _allMarks.postValue(new)
                    _currentMarks.postValue(new)
                }
                else -> {
                    _allMarks.postValue(marks.map { it.toLocal() })
                    _currentMarks.postValue(marks.map { it.toLocal() })
                }
            }
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
                    _currentMarks.value = _currentMarks.value.orEmpty() + mark.data.toLocal()
                    _allMarks.value = _allMarks.value.orEmpty() + mark.data.toLocal()
                    getMarks(sortType = SortType.NONE)
                }
                else -> handleError(mark)
            }
            _loading.postValue(false)
        }
    }

    fun likeMark(mark: MarkLocal) {
        viewModelScope.launch {
            markRepository.addFavoriteMark(mark.toRemote())

            val allList = _allMarks.value?.toMutableList()
            allList?.let {
                val id = mark.id
                for (i in allList.indices) {
                    if (allList[i].id == id)
                        allList[i] = allList[i].copy(isFavorite = !allList[i].isFavorite)
                }
                _allMarks.postValue(it)
            }

            val currentList = _currentMarks.value?.toMutableList()
            currentList?.let {
                val id = mark.id
                for (i in currentList.indices) {
                    if (currentList[i].id == id)
                        currentList[i] = currentList[i].copy(isFavorite = !currentList[i].isFavorite)
                }
                _currentMarks.postValue(it)
            }
        }
    }

    fun getMarkById(markId: Long): MarkLocal? {
        return _currentMarks.value?.find { it.id == markId }
    }

    fun getCurrentUserId(): Long {
        return _isCurrentUser.value?.id ?: -1L
    }

    fun deleteMark(id: Long) {
        viewModelScope.launch {
            when (val result = markRepository.deleteMark(id)) {
                is ResultState.Success -> {
                    _currentMarks.value = _currentMarks.value?.filter { it.id != id }
                    _allMarks.value = _allMarks.value?.filter { it.id != id }
                }
                else -> handleError(result)
            }
        }
    }

    fun addToSubscribe(subscribe: UserResponse) {
        viewModelScope.launch {
            when (val result = markRepository.addSubscribe(subscribe)) {
                is ResultState.Success -> {}
                else -> handleError(result)
            }
        }
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