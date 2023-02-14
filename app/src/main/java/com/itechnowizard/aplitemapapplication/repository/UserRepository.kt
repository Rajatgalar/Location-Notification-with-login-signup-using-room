package com.itechnowizard.aplitemapapplication.repository

import com.itechnowizard.aplitemapapplication.dao.UsersDao
import com.itechnowizard.aplitemapapplication.model.Users
import javax.inject.Inject

interface UserRepository {

    fun addUser(users: Users):Long

    fun verifyLoginUser(mobNum:String,password:String): Users

    fun getUserDataDetails(id:Long):Users

    fun updateUserData(users: Users): Int
}

class UserRepositoryImpl @Inject constructor(
    private  var usersDao: UsersDao
):UserRepository {

    override fun addUser(users: Users): Long {
        return usersDao.insertUser(users)
    }

    override fun verifyLoginUser(username: String, password: String): Users {
        return usersDao.readLoginData(username = username, password = password)
    }

    override fun getUserDataDetails(id: Long): Users {
        return usersDao.getUserDataDetails(id)
    }

    override fun updateUserData(users: Users) : Int{
        return usersDao.updateUserData(users)
    }
}