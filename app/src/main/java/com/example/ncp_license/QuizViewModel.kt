package com.example.ncp_license

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ncp_license.data.QuestionRepository
import com.example.ncp_license.models.Question

class QuizViewModel : ViewModel() {
    private val questions = QuestionRepository.getQuestions()
    private var currentQuestionIndex = 0

    private val _currentQuestion = MutableLiveData<Question>()
    val currentQuestion: LiveData<Question> get() = _currentQuestion

    private val _isAnswerCorrect = MutableLiveData<Boolean?>()
    val isAnswerCorrect: LiveData<Boolean?> get() = _isAnswerCorrect

    init {
        loadQuestion()
    }

    fun checkAnswer(selectedOptionIndex: Int) {
        val correctAnswer = _currentQuestion.value?.correctAnswer
        _isAnswerCorrect.value = correctAnswer == selectedOptionIndex
    }

    fun loadQuestion() {
        if (currentQuestionIndex < questions.size) {
            _currentQuestion.value = questions[currentQuestionIndex]
            _isAnswerCorrect.value = null
        }
    }

    fun nextQuestion() {
        if (currentQuestionIndex < questions.size - 1) {
            currentQuestionIndex++
            loadQuestion()
        }
    }
}