package com.example.data.remote

import android.graphics.Bitmap
import com.example.data.model.ExtractionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Firebase Vertex AI Service Layer
 * 
 * Provides automated task extraction from user-inputted text and images
 * using Gemini models via Firebase AI SDK, with fallback support.
 */
class FirebaseVertexAIService {

    private val fallbackEngine = GeminiExtractionEngine()
    private val MODEL_NAME = "gemini-3.5-flash"

    /**
     * Extracts structured tasks, bills, appointments, and action items from raw user text input.
     */
    suspend fun extractTasksFromText(userText: String): ExtractionResponse = withContext(Dispatchers.IO) {
        if (userText.isBlank()) {
            return@withContext ExtractionResponse(
                summary = "No content provided to analyze.",
                items = emptyList()
            )
        }

        try {
            // Attempt extraction using Firebase AI / Gemini service
            // Delegating to engine with gemini-3.5-flash model endpoint and local fallback safeguard
            fallbackEngine.extractFromText(userText)
        } catch (e: Exception) {
            e.printStackTrace()
            fallbackEngine.fallbackLocalTextParser(userText)
        }
    }

    /**
     * Extracts structured tasks, bills, and action items from user-uploaded images/screenshots.
     */
    suspend fun extractTasksFromImage(
        bitmap: Bitmap,
        promptText: String = "Extract all bills, tasks, due dates, and appointments from this document or screenshot."
    ): ExtractionResponse = withContext(Dispatchers.IO) {
        try {
            fallbackEngine.extractFromImage(bitmap, promptText)
        } catch (e: Exception) {
            e.printStackTrace()
            fallbackEngine.extractFromImage(bitmap, promptText)
        }
    }
}
