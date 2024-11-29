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
        val flashcard = TaggedFlashCard("What is the capital of France?", "Paris",
            listOf("geography", "easy"))
        assertTrue(flashcard.isTagged("geography"))
        assertFalse(flashcard.isTagged("hard"))
    }

    @Test
    fun testFileFormat() {
        val flashcard = TaggedFlashCard("What is the capital of France?", "Paris",
            listOf("geography", "easy"))
        assertEquals("What is the capital of France?|Paris|geography,easy",
            flashcard.fileFormat())
    }

    @Test
    fun testInitialState() {
        val flashcard = TaggedFlashCard("Question?", "Answer", listOf("tag"))
        val deck = TFCListDeck("Test Deck", listOf(flashcard), true)
        assertEquals(DeckState.QUESTION, deck.getState())
        assertEquals("Question?", deck.getText())
    }

    @Test
    fun testFlip() {
        val flashcard = TaggedFlashCard("Question?", "Answer", listOf("tag"))
        val deck = TFCListDeck("Test Deck", listOf(flashcard), true)
        val flippedDeck = deck.flip()
        assertEquals(DeckState.ANSWER, flippedDeck.getState())
        assertEquals("Answer", flippedDeck.getText())
    }

    @Test
    fun testNextCorrect() {
        val card1 = TaggedFlashCard("Q1", "A1", listOf("tag"))
        val card2 = TaggedFlashCard("Q2", "A2", listOf("tag"))
        val deck = TFCListDeck("Test Deck", listOf(card1, card2), true)
        val newDeck = deck.next(true)
        assertEquals(1, newDeck.getSize())
        assertEquals("Q2", newDeck.getText())
    }

    @Test
    fun testExhaustion() {
        val card = TaggedFlashCard("Q", "A", listOf("tag"))
        val deck = TFCListDeck("Test Deck", listOf(card), true)
        val exhaustedDeck = deck.next(true)
        assertEquals(DeckState.EXHAUSTED, exhaustedDeck.getState())
        assertNull(exhaustedDeck.getText())
    }

    @Test
    fun testDeckFiltering() {
        val geographyDeck = tfcListAll.filter { it.tags.contains(tagGeo) }
        val easyDeck = tfcListAll.filter { it.tags.contains(tagEasy) }
        val hardDeck = tfcListAll.filter { it.tags.contains(tagHard) }

        assertEquals(7, geographyDeck.size)
        assertEquals(2, easyDeck.size)
        assertEquals(2, hardDeck.size)
    }

    @Test
    fun testEmptyDeck() {
        val emptyDeck = TFCListDeck("Empty Deck", listOf(), true)
        assertEquals(DeckState.EXHAUSTED, emptyDeck.getState())
        assertNull(emptyDeck.getText())
    }

    @Test
    fun testLargeDeckPerformance() {
        val largeDeck = TFCListDeck("Large Deck", (1..1000).map {
            TaggedFlashCard("Q$it", "A$it", listOf())
        }, true)
        assertEquals(1000, largeDeck.getSize())
        assertEquals("Q1", largeDeck.getText())
    }

}