package com.rezio23.translatex.model

data class Language(
    val code: String,
    val name: String
)

data class TranslationResult(
    val translatedText: String,
    val sourceLanguage: String,
    val targetLanguage: String
)

val SUPPORTED_LANGUAGES = listOf(
    Language("auto", "Auto Detect"),
    Language("km", "Khmer"),
    Language("en", "English"),
    Language("zh-CN", "Chinese (Simplified)"),
    Language("es", "Spanish"),
    Language("fr", "French"),
    Language("de", "German"),
    Language("it", "Italian"),
    Language("pt", "Portuguese"),
    Language("ru", "Russian"),
    Language("ja", "Japanese"),
    Language("ko", "Korean"),
    Language("ar", "Arabic"),
    Language("hi", "Hindi"),
    Language("tr", "Turkish"),
    Language("pl", "Polish"),
    Language("nl", "Dutch"),
    Language("sv", "Swedish"),
    Language("da", "Danish"),
    Language("fi", "Finnish"),
    Language("no", "Norwegian"),
    Language("id", "Indonesian"),
    Language("ms", "Malay"),
    Language("th", "Thai"),
    Language("vi", "Vietnamese"),
    Language("uk", "Ukrainian")
)
