package com.example.part4_chapter5.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "GithubRepository")
data class GithubRepoEntity(
    val name:String,
    @PrimaryKey val fullName:String,
    @Embedded val owner:GithubOwner,
    val description:String?,
    val language: String?,
    val updatedAt:String,
    val stargazerCount:Int
)
