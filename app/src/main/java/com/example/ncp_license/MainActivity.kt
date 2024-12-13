package com.example.ncp_license

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ncp_license.ui.theme.NCPLicenseTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// 데이터 클래스
data class Question(
    val question: String,
    val options: List<String>,
    val answer: Int
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val questions = loadQuestions(this)

        setContent {
            NCPLicenseTheme {
                Surface {
                    QuizScreen(questions)
                }
            }
        }
    }

    private fun loadQuestions(context: Context): List<Question> {
        val json = context.assets.open("questions.json").bufferedReader().use { it.readText() }
        val questionType = object : TypeToken<List<Question>>() {}.type
        return Gson().fromJson(json, questionType)
    }
}

@Composable
fun QuizScreen(questions: List<Question>) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedOption by remember { mutableStateOf(-1) }
    var isDialogOpen by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var currentFilter by remember { mutableStateOf("All") } // "All", "Correct", "Wrong", "Unanswered"
    val history = remember { mutableStateMapOf<Int, MutableList<Boolean>>() }

    val filteredQuestions = when (currentFilter) {
        "Correct" -> history.filterValues { it.lastOrNull() == true }.keys.map { questions[it] }
        "Wrong" -> history.filterValues { it.lastOrNull() == false }.keys.map { questions[it] }
        "Unanswered" -> questions.indices.filterNot { history.containsKey(it) }.map { questions[it] }
        else -> questions
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상태 필터 버튼
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            listOf("All", "Correct", "Wrong", "Unanswered").forEach { type ->
                Button(onClick = { currentFilter = type }) {
                    Text(text = type)
                }
            }
        }

        // 필터링된 문제 목록 표시
        if (currentFilter != "All") {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(filteredQuestions) { question ->
                    val questionIndex = questions.indexOf(question)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                currentFilter = "All" // 필터를 초기화하여 문제 풀기 화면으로 이동
                                currentQuestionIndex = questionIndex
                            }
                    ) {
                        Text(text = "문제 ${questionIndex + 1}: ${question.question}", fontSize = 18.sp)
                    }
                }
            }
        } else {
            // 현재 문제 화면 표시
            val currentQuestion = questions[currentQuestionIndex]

            // 문제 번호 및 풀이 기록
            Text(
                text = "문제 ${currentQuestionIndex + 1} / ${questions.size}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (history.containsKey(currentQuestionIndex)) {
                Row(horizontalArrangement = Arrangement.Center) {
                    Text(text = "풀이 기록: ")
                    history[currentQuestionIndex]?.let { records ->
                        records.takeLast(5).forEach { result ->
                            Text(text = if (result) "O " else "X ", fontSize = 18.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // 문제 텍스트
            Text(
                text = currentQuestion.question,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 선택지 표시
            currentQuestion.options.forEachIndexed { index, option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    RadioButton(
                        selected = selectedOption == index,
                        onClick = { selectedOption = index }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = option, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 제출 버튼
            Button(
                onClick = {
                    isCorrect = (selectedOption == currentQuestion.answer)
                    isDialogOpen = true
                    if (!history.containsKey(currentQuestionIndex)) {
                        history[currentQuestionIndex] = mutableListOf()
                    }
                    history[currentQuestionIndex]?.add(isCorrect)
                },
                enabled = selectedOption >= 0
            ) {
                Text(text = "제출하기", fontSize = 18.sp)
            }

            // 정답/오답 팝업
            if (isDialogOpen) {
                Dialog(onDismissRequest = { isDialogOpen = false }) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        shadowElevation = 8.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (isCorrect) "정답입니다!" else "오답입니다.",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = {
                                isDialogOpen = false
                                selectedOption = -1
                                if (currentQuestionIndex < questions.size - 1) {
                                    currentQuestionIndex++
                                } else {
                                    currentQuestionIndex = 0
                                }
                            }) {
                                Text(text = "다음 문제로", fontSize = 18.sp)
                            }
                        }
                    }
                }
            }
        }

        // 문제 이동 버튼 및 문제 번호 이동 기능
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = {
                    if (currentQuestionIndex > 0) {
                        currentQuestionIndex--
                        selectedOption = -1
                    }
                }) {
                    Text(text = "이전 문제")
                }
                Button(onClick = {
                    if (currentQuestionIndex < questions.size - 1) {
                        currentQuestionIndex++
                        selectedOption = -1
                    }
                }) {
                    Text(text = "다음 문제")
                }
            }

            // 문제 번호로 이동
            LazyRow(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                items(questions.indices.toList()) { index ->
                    Button(
                        onClick = { currentQuestionIndex = index },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(text = "${index + 1}")
                    }
                }
            }
        }
    }
}