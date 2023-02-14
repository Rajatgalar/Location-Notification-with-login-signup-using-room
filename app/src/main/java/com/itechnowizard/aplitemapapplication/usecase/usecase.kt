package com.itechnowizard.aplitemapapplication.usecase

import com.itechnowizard.aplitemapapplication.model.Users
import com.itechnowizard.aplitemapapplication.repository.UserRepository


interface UsersUseCase {
    suspend fun addUser(users: Users): Long
    suspend fun getUserLoginVerify(mobNum: String, password: String): Users
    suspend fun getUserData(id: Long): Users
    suspend fun updateUserData(users: Users): Int
}


class UsersUseCaseImpl (private var userRepository: UserRepository):UsersUseCase{
    override suspend fun addUser(users: Users): Long {
        val id= userRepository.addUser(users)
        return id
    }

    override suspend fun getUserLoginVerify(mobNum:String, password:String): Users {
        return userRepository.verifyLoginUser(mobNum, password)
    }

    override suspend fun getUserData(id: Long): Users {
        return userRepository.getUserDataDetails(id)
    }

    override suspend fun updateUserData(users: Users): Int {
       val id = userRepository.updateUserData(users)
        return id
    }

}