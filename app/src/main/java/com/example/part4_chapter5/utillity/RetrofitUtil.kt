package com.example.part4_chapter5.utillity

import com.example.part4_chapter5.data.response.Url
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitUtil {
    val authApiService: AuthApiService by lazy {
        getGitHubAuthRetrofit().create(AuthApiService::class.java)
    }
    val githubApiService: GithubApiService by lazy {
        getGitHuRetrofit().create(GithubApiService::class.java)
    }

    private fun getGitHubAuthRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Url.GITHUB_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create()
                )
            )
            .build()
    }

    private fun getGitHuRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Url.GITHUB_API_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create()
                )
            )
            .build()
    }


}