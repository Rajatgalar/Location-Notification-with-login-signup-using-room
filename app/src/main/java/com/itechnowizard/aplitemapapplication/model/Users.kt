package com.itechnowizard.aplitemapapplication.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Users")
data class Users(

    @PrimaryKey(autoGenerate = true)
    val id: Long=0,
    val fname:String,
    val lname:String,
    val username:String,
    val email:String,
    val password:String,
    val gender:String,
    val address:String
)
