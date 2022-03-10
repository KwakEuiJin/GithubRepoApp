package com.example.part4_chapter5.data.response

import com.example.part4_chapter5.data.entity.GithubRepoEntity

data class GithubRepoSearchResponse (
    val totalCount:Int,
    val items: List<GithubRepoEntity>
    )