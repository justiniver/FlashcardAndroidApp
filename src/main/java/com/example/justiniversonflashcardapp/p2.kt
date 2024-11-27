package com.example.justiniversonflashcardapp

// (just useful values for the separation characters)
const val sepCard = "|"
const val sepTag = ","

// A card has a front, back, and associated tags
data class TaggedFlashCard(val front: String, val back: String, val tags: List<String>) {
    fun isTagged(tag: String): Boolean = tags.any { it == tag }

    fun fileFormat(): String = "${front}${sepCard}${back}${sepCard}${tags.joinToString(sepTag)}"
}

// The deck is either exhausted, showing the question, or showing the answer
enum class DeckState {
    EXHAUSTED,
    QUESTION,
    ANSWER,
}

// Basic functionality of any deck
interface IDeck {
    // The state of the deck
    fun getState(): DeckState

    // The currently visible text (or null if exhausted)
    fun getText(): String?

    // The number of question/answer pairs (does not change when questions are cycled to the end of the deck)
    fun getSize(): Int

    // Shifts from question -> answer (if not QUESTION state, returns the same IDeck)
    fun flip(): IDeck

    // Shifts from answer -> next question (or exhaustion); if the current question was correct it is discarded, otherwise cycled to the end of the deck (if not ANSWER state, returns the same IDeck)
    fun next(correct: Boolean): IDeck

    // Get the name of the deck
    fun getDeckName(): String
}

// Takes in a list of tagged flashcards and a boolean which determines whether the flashcard is on the front utilizing the IDeck interface
data class TFCListDeck(val name: String, val tfcList: List<TaggedFlashCard>, val isFront: Boolean) : IDeck {
    // Checks if list is empty and if not checks boolean input and returns appropriate deck state
    override fun getState(): DeckState {
        return when {
            tfcList.isEmpty() -> DeckState.EXHAUSTED
            isFront -> DeckState.QUESTION
            else -> DeckState.ANSWER
        }
    }

    // Checks if list is empty and returns null if empty, and if not checks boolean input and returns appropriate string
    override fun getText(): String? {
        return if (tfcList.isEmpty()) null else if (isFront) tfcList.first().front else tfcList.first().back
    }

    // Returns list size as integer
    override fun getSize(): Int = tfcList.size

    // If on front of flashcard goes to back and vice versa
    override fun flip(): IDeck {
        return TFCListDeck(name, tfcList, !isFront)
    }

    // Drops current card and returns next card; if user gets question wrong, adds flashcard to back of list
    override fun next(correct: Boolean): IDeck {
        return if (correct) {
            TFCListDeck(name, tfcList.drop(1), true)
        } else {
            TFCListDeck(name, tfcList.drop(1) + tfcList.first(), true)
        }
    }

    override fun getDeckName(): String {
        return name
    }
}

// You can add other deck types here following the same pattern as TFCListDeck.
