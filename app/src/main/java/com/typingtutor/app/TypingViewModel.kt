package com.typingtutor.app

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

data class LessonResult(
    val lessonId: Int,
    val wpm: Int,
    val accuracy: Int,
    val bestWpm: Int
)

class TypingViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("typing_tutor_progress", Context.MODE_PRIVATE)

    private val _typedText = mutableStateOf("")
    val typedText: State<String> get() = _typedText

    private val _startTimeMs = mutableStateOf(0L)
    private val _isRunning = mutableStateOf(false)
    val isRunning: State<Boolean> get() = _isRunning

    private val _result = mutableStateOf<LessonResult?>(null)
    val result: State<LessonResult?> get() = _result

    var currentLessonId: Int = -1

    fun onTextChanged(newText: String, targetText: String) {
        if (!_isRunning.value && newText.isNotEmpty()) {
            _isRunning.value = true
            _startTimeMs.value = System.currentTimeMillis()
        }

        val clipped = if (newText.length > targetText.length) newText.substring(0, targetText.length) else newText
        _typedText.value = clipped

        if (clipped.length == targetText.length && targetText.isNotEmpty()) {
            finishLesson(targetText)
        }
    }

    fun reset() {
        _typedText.value = ""
        _isRunning.value = false
        _startTimeMs.value = 0L
        _result.value = null
    }

    private fun finishLesson(targetText: String) {
        _isRunning.value = false
        val elapsedMs = System.currentTimeMillis() - _startTimeMs.value
        val elapsedMinutes = (elapsedMs / 1000.0 / 60.0).coerceAtLeast(0.01)

        val typed = _typedText.value
        val correctChars = typed.indices.count { idx -> idx < targetText.length && typed[idx] == targetText[idx] }
        val accuracy = if (typed.isNotEmpty()) ((correctChars.toDouble() / typed.length) * 100).toInt() else 0

        val wpm = ((typed.length / 5.0) / elapsedMinutes).toInt()

        val lessonId = currentLessonId
        val bestKey = "best_wpm_$lessonId"
        val previousBest = prefs.getInt(bestKey, 0)
        val newBest = maxOf(previousBest, wpm)
        prefs.edit().putInt(bestKey, newBest).putInt("acc_$lessonId", accuracy).apply()

        _result.value = LessonResult(lessonId, wpm, accuracy, newBest)
    }

    fun bestWpmFor(lessonId: Int): Int = prefs.getInt("best_wpm_$lessonId", 0)
    fun bestAccuracyFor(lessonId: Int): Int = prefs.getInt("acc_$lessonId", 0)

    fun overallStats(): Pair<Int, Int> {
        val completed = LessonRepository.lessons.count { prefs.contains("best_wpm_${it.id}") }
        val avgWpm = LessonRepository.lessons
            .mapNotNull { if (prefs.contains("best_wpm_${it.id}")) prefs.getInt("best_wpm_${it.id}", 0) else null }
            .let { if (it.isEmpty()) 0 else it.average().toInt() }
        return Pair(completed, avgWpm)
    }
}
