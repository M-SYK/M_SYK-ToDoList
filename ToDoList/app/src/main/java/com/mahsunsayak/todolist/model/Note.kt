package com.mahsunsayak.todolist.model

data class Note(
    var id: String = "",
    val userId: String = "",
    val text: String = "",
    val fullText: String = "",
    val priority: Int = 1,
    val isCompleted: Boolean = false,
    var documentId: String = ""
) {

}
