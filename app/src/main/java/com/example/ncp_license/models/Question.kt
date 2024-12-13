package com.example.ncp_license.models

data class Question(
    val id: Int,
    val questionText: String,
    val options: List<String>,
    val correctAnswer: Int // 정답 인덱스
)