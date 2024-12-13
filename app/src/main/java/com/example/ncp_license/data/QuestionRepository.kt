package com.example.ncp_license.data

import com.example.ncp_license.models.Question

object QuestionRepository {
    fun getQuestions(): List<Question> {
        return listOf(
            Question(
                id = 1,
                questionText = "네이버클라우드의 주요 서비스는 무엇인가요?",
                options = listOf("컴퓨팅", "스토리지", "네트워크", "모두 해당"),
                correctAnswer = 3
            ),
            Question(
                id = 2,
                questionText = "클라우드 환경에서 제공되지 않는 것은?",
                options = listOf("자동차", "컴퓨팅", "데이터베이스", "스토리지"),
                correctAnswer = 0
            )
        )
    }
}