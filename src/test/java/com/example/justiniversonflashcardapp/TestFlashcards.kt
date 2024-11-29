package com.example.justiniversonflashcardapp

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TestFlashcards {
    @Test
    fun testIsTagged() {
        val flashcard = TaggedFlashCard("What is the capital of France?", "Paris", listOf("geography", "easy"))
        assertTrue(flashcard.isTagged("geography"))
        assertFalse(flashcard.isTagged("hard"))
    }

}