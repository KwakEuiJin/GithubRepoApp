package com.example.part4_chapter5.data.dao

import androidx.room.*
import com.example.part4_chapter5.data.entity.GithubRepoEntity

@Dao
interface RepositoryDao {
    @Insert
    suspend fun insert(repo:GithubRepoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repoList:List<GithubRepoEntity>)

    @Query("SELECT * FROM githubrepository ")
    suspend fun getHistory():List<GithubRepoEntity>

    @Query("SELECT *FROM githubrepository WHERE fullName = :fullName")
    suspend fun getRepository(fullName:String): GithubRepoEntity?

    @Query("DELETE FROM githubrepository where fullName = :fullName")
    suspend fun remove(fullName: String)

    /*@Delete
    suspend fun remove(repo: GithubRepoEntity)*/

    @Query("DELETE FROM githubrepository ")
    suspend fun clearAll()

}