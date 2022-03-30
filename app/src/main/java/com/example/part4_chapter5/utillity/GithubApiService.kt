package com.example.part4_chapter5.utillity

import com.example.part4_chapter5.data.entity.GithubRepoEntity
import com.example.part4_chapter5.data.response.GithubRepoSearchResponse
import com.example.part4_chapter5.data.response.TokenResponse
import retrofit2.Response
import retrofit2.http.*

interface GithubApiService {
    @GET("search/repositories")
    suspend fun searchRepositories(@Query("q") query: String):Response<GithubRepoSearchResponse>

    @GET("repos/{owner}/{name}")
    suspend fun getRepository(
        @Path("owner") ownerLogin:String,
        @Path("name") repoName:String
    ):Response<GithubRepoEntity>

    @GET("user")
    suspend fun getLoginInfo(
        @Header("Authorization") token:String
    ):Response<TokenResponse>




}