package com.example.justiniversonflashcardapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

fun levenshteinDistance(a: String, b: String): Int {
    val dp = Array(a.length + 1) { IntArray(b.length + 1) }
    for (i in dp.indices) dp[i][0] = i
    for (j in dp[0].indices) dp[0][j] = j

    for (i in 1..a.length) {
        for (j in 1..b.length) {
            dp[i][j] = if (a[i - 1] == b[j - 1]) {
                dp[i - 1][j - 1]
            } else {
                1 + minOf(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1])
            }
        }
    }
    return dp[a.length][b.length]
}

fun isAnswerCorrect(userInput: String, correctAnswer: String, threshold: Int = 2): Boolean {
    val distance = levenshteinDistance(userInput.lowercase(), correctAnswer.lowercase())
    return distance <= threshold
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val geographyDeck = tfcListAll.filter { it.tags.contains(tagGeo) }
        val easyDeck = tfcListAll.filter { it.tags.contains(tagEasy) }
        val hardDeck = tfcListAll.filter { it.tags.contains(tagHard) }

        val decks = listOf(
            TFCListDeck("Geography Deck", geographyDeck, true),
            TFCListDeck("Easy Deck", easyDeck, true),
            TFCListDeck("Hard Deck", hardDeck, true)
        )

        setContent {
            var selectedDeck by remember { mutableStateOf<IDeck?>(null) }

            if (selectedDeck == null) {
                DeckSelectionScreen(decks) { selectedDeck = it }
            } else {
                SimpleFlashcards(deck = selectedDeck!!,
                    returnToDeckSelection = { selectedDeck = null })
            }
        }
    }
}

@Composable
fun DeckSelectionScreen(decks: List<IDeck>, onDeckSelected: (IDeck) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Choose a Deck to Study", style = MaterialTheme.typography.headlineMedium)
        decks.forEach { deck ->
            Button(
                onClick = { onDeckSelected(deck) },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(deck.getDeckName())
            }
        }
    }
}

@Composable
fun SimpleFlashcards(deck: IDeck, returnToDeckSelection: () -> Unit) {
    var currentCard by remember { mutableStateOf(deck) }
    var feedback by remember { mutableStateOf("") }
    var attempts by remember { mutableStateOf(0) }

    fun handleSubmitAnswer(userInput: String) {
        if (currentCard.getState() == DeckState.QUESTION) {
            val correctAnswer = currentCard.flip().getText() ?: ""
            val isCorrect = isAnswerCorrect(userInput, correctAnswer)
            feedback = if (isCorrect) {
                "Correct! The answer is $correctAnswer."
            } else {
                "Incorrect. The correct answer is $correctAnswer."
            }
            attempts++
        }
    }

    fun handleNextCard() {
        if (currentCard.getState() == DeckState.ANSWER) {
            currentCard = currentCard.next(feedback.contains("Correct"))
        } else if (currentCard.getState() == DeckState.EXHAUSTED) {
            returnToDeckSelection()
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentCard.getText() ?: "",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(70.dp)
                )
            }
        }
        FlashcardButtons(
            currentCard = currentCard,
            feedback = feedback,
            onSubmitAnswer = ::handleSubmitAnswer,
            onNextCard = ::handleNextCard
        )
    }
}

@Composable
fun FlashcardButtons(
    currentCard: IDeck,
    feedback: String,
    onSubmitAnswer: (String) -> Unit,
    onNextCard: () -> Unit
) {
    var userResponse by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (currentCard.getState() == DeckState.QUESTION) {
            OutlinedTextField(
                value = userResponse,
                onValueChange = { userResponse = it },
                label = { Text("Your Answer") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    onSubmitAnswer(userResponse.trim())
                    userResponse = ""
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Submit")
            }
        } else if (currentCard.getState() == DeckState.ANSWER) {
            Text(feedback, style = MaterialTheme.typography.bodyLarge)
            Button(
                onClick = onNextCard,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Next")
            }
        } else if (currentCard.getState() == DeckState.EXHAUSTED) {
            Text(
                "You've completed the deck!",
                style = MaterialTheme.typography.headlineMedium
            )
            Button(
                onClick = onNextCard,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Return to Deck Selection")
            }
        }
    }
}