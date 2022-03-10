package com.example.part4_chapter5.data.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    private const val DB_NAME = "github_repository_app.db"

    fun provideDB(applicationContext: Context) = Room.databaseBuilder(
        applicationContext,
        SimpleGithubDataBase::class.java, DB_NAME
    ).build()

}