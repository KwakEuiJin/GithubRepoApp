package com.example.part4_chapter5.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.part4_chapter5.data.dao.RepositoryDao
import com.example.part4_chapter5.data.entity.GithubRepoEntity

@Database(entities = [GithubRepoEntity::class], version = 1)
abstract class SimpleGithubDataBase: RoomDatabase() {
    abstract fun repositoryDao(): RepositoryDao
}