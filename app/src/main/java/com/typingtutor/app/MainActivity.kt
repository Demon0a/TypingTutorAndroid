package com.typingtutor.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                TypingTutorApp()
            }
        }
    }
}

sealed class Screen {
    object Home : Screen()
    data class Practice(val lesson: Lesson) : Screen()
}

@Composable
fun TypingTutorApp(viewModel: TypingViewModel = viewModel()) {
    var screen by remember { mutableStateOf<Screen>(Screen.Home) }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF5F7FA)) {
        when (val s = screen) {
            is Screen.Home -> HomeScreen(
                viewModel = viewModel,
                onLessonClick = {
                    viewModel.currentLessonId = it.id
                    viewModel.reset()
                    screen = Screen.Practice(it)
                }
            )
            is Screen.Practice -> PracticeScreen(
                lesson = s.lesson,
                viewModel = viewModel,
                onBack = { screen = Screen.Home }
            )
        }
    }
}

@Composable
fun HomeScreen(viewModel: TypingViewModel, onLessonClick: (Lesson) -> Unit) {
    val (completed, avgWpm) = viewModel.overallStats()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Typing Tutor", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("English typing practice", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF2D5BFF))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatBlock("Lessons Completed", "$completed / ${LessonRepository.lessons.size}")
            StatBlock("Average WPM", "$avgWpm")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            LessonRepository.categories().forEach { category ->
                item {
                    Text(
                        text = category,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2D5BFF),
                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                    )
                }
                items(LessonRepository.byCategory(category)) { lesson ->
                    val best = viewModel.bestWpmFor(lesson.id)
                    LessonCard(lesson = lesson, bestWpm = best, onClick = { onLessonClick(lesson) })
                }
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun StatBlock(label: String, value: String) {
    Column {
        Text(value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(label, color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
    }
}

@Composable
fun LessonCard(lesson: Lesson, bestWpm: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(lesson.title, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                Text(lesson.description, fontSize = 12.sp, color = Color.Gray)
            }
            if (bestWpm > 0) {
                Text("$bestWpm WPM", fontSize = 13.sp, color = Color(0xFF2D5BFF), fontWeight = FontWeight.Bold)
            } else {
                Text("New", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun PracticeScreen(lesson: Lesson, viewModel: TypingViewModel, onBack: () -> Unit) {
    val typed = viewModel.typedText.value
    val result = viewModel.result.value

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("← Back") }
            Spacer(modifier = Modifier.weight(1f))
            Text(lesson.category, color = Color.Gray, fontSize = 13.sp)
        }

        Text(lesson.title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(text = buildHighlightedText(lesson.text, typed), fontSize = 18.sp, lineHeight = 28.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = typed,
            onValueChange = { viewModel.onTextChanged(it, lesson.text) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Type here") },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = { viewModel.reset() }, modifier = Modifier.fillMaxWidth()) {
            Text("Restart Lesson")
        }

        if (result != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF2D5BFF))) {
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    Text("Lesson Complete!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        StatBlock("Speed", "${result.wpm} WPM")
                        StatBlock("Accuracy", "${result.accuracy}%")
                        StatBlock("Best", "${result.bestWpm} WPM")
                    }
                }
            }
        }
    }
}

@Composable
fun buildHighlightedText(target: String, typed: String): AnnotatedString {
    return androidx.compose.ui.text.buildAnnotatedString {
        for (i in target.indices) {
            val char = target[i]
            when {
                i < typed.length && typed[i] == char -> {
                    withStyle(SpanStyle(color = Color(0xFF1FA34A), fontWeight = FontWeight.Bold)) { append(char) }
                }
                i < typed.length && typed[i] != char -> {
                    withStyle(SpanStyle(color = Color.White, background = Color(0xFFE53935))) { append(char) }
                }
                i == typed.length -> {
                    withStyle(SpanStyle(color = Color.Black, background = Color(0xFFFFE082))) { append(char) }
                }
                else -> {
                    withStyle(SpanStyle(color = Color(0xFFAAAAAA))) { append(char) }
                }
            }
        }
    }
}
