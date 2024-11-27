package com.example.justiniversonflashcardapp

// Flashcard data
const val qCB = "What is the capital of Cuba?"
const val aCB = "Havana"

const val qJP = "What is the capital of Japan?"
const val aJP = "Tokyo"

const val qMN = "What is the capital of Mongolia?"
const val aMN = "Ulaanbaatar"

const val qRB = "What is the largest river by discharge volume in the world?"
const val aRB = "Amazon River"

const val qHV = "Which country is home to the highest waterfall in the world?"
const val aHV = "Venezuela (Angel Falls)"

const val qDS = "What is the deepest sea in the world?"
const val aDS = "Philippine Sea"

const val qML = "Which country has the most lakes in the world?"
const val aML = "Canada"


const val tagGeo = "geography"
const val tagEasy = "easy"
const val tagHard = "hard"

val tfcCB = TaggedFlashCard(qCB, aCB, listOf(tagGeo))
val tfcJP = TaggedFlashCard(qJP, aJP, listOf(tagGeo, tagEasy))
val tfcMN = TaggedFlashCard(qMN, aMN, listOf(tagGeo, tagHard))
val tfcRB = TaggedFlashCard(qRB, aRB, listOf(tagGeo))
val tfcHV = TaggedFlashCard(qHV, aHV, listOf(tagGeo, tagHard))
val tfcDS = TaggedFlashCard(qDS, aDS, listOf(tagGeo, tagEasy))
val tfcML = TaggedFlashCard(qML, aML, listOf(tagGeo))

val tfcListAll = listOf(tfcCB, tfcJP, tfcMN, tfcRB, tfcHV, tfcDS, tfcML)
