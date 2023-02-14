package com.itechnowizard.aplitemapapplication.dao

import androidx.room.*
import com.itechnowizard.aplitemapapplication.model.Users

@Dao
interface UsersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user:Users) : Long

    //checking user exist or not in our db
    @Query("SELECT * FROM Users WHERE username LIKE :username AND password LIKE :password")
    fun readLoginData(username: String, password: String):Users

    //getting user data details
    @Query("select * from users where id Like :id")
    fun getUserDataDetails(id:Long):Users

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateUserData(user: Users): Int


}