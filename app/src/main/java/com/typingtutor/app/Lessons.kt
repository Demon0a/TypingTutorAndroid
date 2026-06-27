package com.typingtutor.app

data class Lesson(
    val id: Int,
    val title: String,
    val category: String,
    val text: String,
    val description: String
)

object LessonRepository {

    val lessons: List<Lesson> = listOf(
        // --- HOME ROW ---
        Lesson(1, "Home Row Basics", "Home Row",
            "asdf jkl; asdf jkl; fjfj dkdk slsl a;a;",
            "Learn the home row keys: A S D F J K L ;"),
        Lesson(2, "Home Row Words", "Home Row",
            "ask dad lad sad fall jak salad flask glass alas",
            "Practice forming simple words using home row keys."),

        // --- TOP ROW ---
        Lesson(3, "Top Row Basics", "Top Row",
            "qwer tyui op qwer tyui op wert yuio plop",
            "Learn the top row keys: Q W E R T Y U I O P"),
        Lesson(4, "Top Row Words", "Top Row",
            "quiet trip wire your power tower quote write quirky",
            "Practice forming words using the top row."),

        // --- BOTTOM ROW ---
        Lesson(5, "Bottom Row Basics", "Bottom Row",
            "zxcv bnm, zxcv bnm, zxzx cvcv bnbn mm,,",
            "Learn the bottom row keys: Z X C V B N M , ."),
        Lesson(6, "Bottom Row Words", "Bottom Row",
            "zoom calm exam comb numb mob vibe maze cave",
            "Practice forming words using the bottom row."),

        // --- NUMBERS ---
        Lesson(7, "Number Row", "Numbers",
            "123 456 789 0 12 34 56 78 90 13 57 24 68",
            "Practice the number row keys without looking."),
        Lesson(8, "Numbers and Symbols", "Numbers",
            "Room 12 has 3 desks, 4 chairs, and 5 lamps for 6 students.",
            "Mix numbers with regular words."),

        // --- COMMON WORDS ---
        Lesson(9, "Common Words I", "Words",
            "the and for are but not you all can had her was one our",
            "Most frequently used English words."),
        Lesson(10, "Common Words II", "Words",
            "out day get has him his how man new now old see two way who",
            "Continue practicing high-frequency words."),

        // --- SENTENCES ---
        Lesson(11, "Short Sentences", "Sentences",
            "The sun is hot. The cat ran fast. We like to read books.",
            "Build speed and rhythm with short sentences."),
        Lesson(12, "Punctuation Practice", "Sentences",
            "Hello, how are you? I am fine, thank you! Let's meet at 5 PM.",
            "Practice commas, periods, question marks, and exclamation marks."),

        // --- PARAGRAPHS ---
        Lesson(13, "Paragraph I", "Paragraphs",
            "Typing is a skill that improves with regular practice. The more you " +
            "type without looking at the keyboard, the faster and more accurate you become.",
            "Practice typing full paragraphs for endurance."),
        Lesson(14, "Paragraph II", "Paragraphs",
            "Good posture and finger placement are essential for fast typing. Keep your " +
            "wrists straight, sit upright, and rest your fingers lightly on the home row keys.",
            "Focus on accuracy while maintaining a steady pace."),

        // --- SPEED TEST ---
        Lesson(15, "Speed Test", "Speed Test",
            "Practice makes perfect. Consistency builds speed. Accuracy comes before speed. " +
            "Type every day to improve your skill. Patience and practice always win.",
            "A timed test to measure your words per minute and accuracy.")
    )

    fun categories(): List<String> = lessons.map { it.category }.distinct()

    fun byCategory(category: String): List<Lesson> = lessons.filter { it.category == category }
}
