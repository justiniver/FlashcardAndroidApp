package com.example.justiniversonflashcardapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

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
                SimpleFlashcards(deck = selectedDeck!!, returnToDeckSelection = { selectedDeck = null })
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
    var expanded by remember { mutableStateOf(false) }
    var front by remember { mutableStateOf(deck.getText() ?: "") }
    var attempts: Int by remember { mutableIntStateOf(0) }
    var topCard: IDeck by remember { mutableStateOf(deck) }

    @Composable
    fun SimpleCard() {
        val textModifier = Modifier
            .padding(vertical = 70.dp)
            .fillMaxWidth()

        fun cardText(getFront: Boolean = false): String {
            return when {
                topCard.getState() == DeckState.EXHAUSTED -> "You've reached the end of the deck.\nQuestions: ${deck.getSize()}, Attempts: $attempts"
                topCard.getState() == DeckState.ANSWER && getFront -> front
                else -> topCard.getText() ?: ""
            }
        }

        fun onClickCard() {
            if (topCard.getState() == DeckState.QUESTION) {
                expanded = !expanded
                front = cardText(true)
                topCard = topCard.flip()
            }
        }

        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClickCard() }
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = cardText(true),
                    textAlign = TextAlign.Center,
                    modifier = textModifier
                )
                AnimatedVisibility(visible = expanded) {
                    Text(
                        text = cardText(false),
                        color = Color(0xFF09A129),
                        textAlign = TextAlign.Center,
                        modifier = textModifier
                    )
                }
            }
        }
    }

    @Composable
    fun FlashcardButtons() {
        val buttonModifier = Modifier.padding(20.dp)

        fun onClickNext(correct: Boolean) {
            expanded = false
            topCard = topCard.next(correct)
            attempts++
        }

        Row(modifier = Modifier.padding(10.dp)) {
            if (topCard.getState() == DeckState.EXHAUSTED) {
                Button(
                    onClick = { returnToDeckSelection() },
                    modifier = buttonModifier
                ) {
                    Text(text = "Exit")
                }
            } else {
                Button(
                    enabled = topCard.getState() == DeckState.ANSWER,
                    onClick = { onClickNext(false) },
                    modifier = buttonModifier
                ) {
                    Text(text = "Incorrect")
                }
                Button(
                    enabled = topCard.getState() == DeckState.ANSWER,
                    onClick = { onClickNext(true) },
                    modifier = buttonModifier
                ) {
                    Text(text = "Correct")
                }
            }
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
        SimpleCard()
        FlashcardButtons()
    }
}
