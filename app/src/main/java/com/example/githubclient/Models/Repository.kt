package com.example.githubclient.Models

data class Repository(
    val name: String?,
    val description: String?,
    val language: String?,
    val forksCount: Int?,
    val stargazersCount: Int?,
    val ownerLogin: String?,
    val ownerAvatarUrl: String?
)