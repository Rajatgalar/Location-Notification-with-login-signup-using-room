package com.itechnowizard.aplitemapapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itechnowizard.aplitemapapplication.model.Users
import com.itechnowizard.aplitemapapplication.resource.Resource
import com.itechnowizard.aplitemapapplication.usecase.UsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor(private var usersUseCase: UsersUseCase) : ViewModel() {

    private val _insertUsersDataStatus = MutableLiveData<Resource<Long>>()
    val insertUsersDataStatus: LiveData<Resource<Long>> = _insertUsersDataStatus

    private val _updateUsersDataStatus = MutableLiveData<Resource<Int>>()
    val updateUsersDataStatus: MutableLiveData<Resource<Long>> = _insertUsersDataStatus

    fun insertUserData(users: Users) {
        viewModelScope.launch {
            _insertUsersDataStatus.postValue(Resource.loading(null))
            try {
                val data = usersUseCase.addUser(users)
                _insertUsersDataStatus.postValue(Resource.success(data, 0))
            } catch (exception: Exception) {
                _insertUsersDataStatus.postValue(Resource.error(null, exception.message!!))
            }
        }
    }

    fun updateUserData(users: Users){
        viewModelScope.launch {
            _updateUsersDataStatus.postValue(Resource.loading(null))
            try {
                val data = usersUseCase.updateUserData(users)
                _updateUsersDataStatus.postValue(Resource.success(data, 0))
            } catch (exception: Exception) {
                _updateUsersDataStatus.postValue(Resource.error(null, exception.message!!))
            }
        }
    }


}