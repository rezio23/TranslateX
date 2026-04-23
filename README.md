# TranslateX 🌐

An Android translation app built with Kotlin using OkHttp and the Free Google Translator API via RapidAPI.

## Features
- Translate text between 24+ languages
- Auto-detect source language
- Swap source/target languages
- Copy translated result to clipboard
- Clear input with one tap
- Auth header injection via OkHttp Interceptor
- Request/response logging via HttpLoggingInterceptor

## Tech Stack
- Kotlin
- MVVM Architecture (ViewModel + LiveData)
- OkHttp (standalone, no Retrofit)
- Custom AuthInterceptor for RapidAPI headers
- HttpLoggingInterceptor for debugging
- Gson for JSON parsing
- ViewBinding
- Material Design 3
- Coroutines (Dispatchers.IO)

## Setup

1. Get a free API key from [RapidAPI - Free Google Translator](https://rapidapi.com/joshimuddin8212/api/free-google-translator)
2. Open `app/src/main/java/com/rezio23/translatex/network/TranslatorClient.kt`
3. Replace `YOUR_RAPIDAPI_KEY` with your actual key:
   ```kotlin
   private const val RAPIDAPI_KEY = "your_actual_key_here"
   ```
4. Sync Gradle and run the app

## Supported Languages
Auto Detect, English, Spanish, French, German, Italian, Portuguese, Russian, Japanese, Korean, Chinese, Arabic, Hindi, Turkish, Polish, Dutch, Swedish, Danish, Finnish, Norwegian, Indonesian, Malay, Thai, Vietnamese, Ukrainian
