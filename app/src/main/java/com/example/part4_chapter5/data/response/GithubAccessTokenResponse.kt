package com.example.part4_chapter5.data.response

data class GithubAccessTokenResponse (
    val accessToken:String,
    val scope: String,
    val tokenType: String)