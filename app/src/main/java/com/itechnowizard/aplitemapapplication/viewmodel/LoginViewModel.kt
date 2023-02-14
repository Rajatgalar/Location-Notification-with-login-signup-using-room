package com.itechnowizard.aplitemapapplication.viewmodel

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
class LoginViewModel @Inject constructor(var usersUseCase: UsersUseCase): ViewModel() {
    private val _GetUserLoginDataStatus = MutableLiveData<Resource<Users>>()

    val getUserLoginDataStatus: MutableLiveData<Resource<Users>> = _GetUserLoginDataStatus
    private val _GetUserProfileDataStatus = MutableLiveData<Resource<Users>>()

    val getUserProfileDataStatus: MutableLiveData<Resource<Users>> = _GetUserProfileDataStatus

    fun getUserLoginDataStatus(username:String,password:String) {
        viewModelScope.launch {
            _GetUserLoginDataStatus.postValue(Resource.loading(null))
            try {
                val data = usersUseCase.getUserLoginVerify(username,password)
                _GetUserLoginDataStatus.postValue(Resource.success(data, 0))
            } catch (exception: Exception) {
                _GetUserLoginDataStatus.postValue(Resource.error(null, exception.message!!))
            }
        }
    }

    fun getUserProfileData(id:Long){
        viewModelScope.launch {
            _GetUserProfileDataStatus.postValue(Resource.loading(null))
            try {
                val data = usersUseCase.getUserData(id)
                _GetUserProfileDataStatus.postValue(Resource.success(data, 0))
            } catch (exception: Exception) {
                _GetUserProfileDataStatus.postValue(Resource.error(null, exception.message!!))
            }
        }
    }


}