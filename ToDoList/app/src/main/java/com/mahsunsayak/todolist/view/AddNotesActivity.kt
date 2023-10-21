package com.mahsunsayak.todolist.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mahsunsayak.todolist.databinding.ActivityAddNotesBinding
import com.mahsunsayak.todolist.model.Note

class AddNotesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNotesBinding
    private lateinit var auth: FirebaseAuth
    var count = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNotesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        // "Save" butonuna tıklandığında çağrılacak fonksiyonu ayarla
        binding.saveNoteButton.setOnClickListener {
            saveNoteClicked()
        }
    }

    private fun saveNoteClicked() {
        val noteText = binding.noteText.text.toString()
        val priorityRadioGroup = binding.priorityRadioGroup
        val selectedRadioButtonId = priorityRadioGroup.checkedRadioButtonId
        val isCompleted = false
        var priority = 0

        // Seçilen RadioButton'a göre öncelik belirle
        if (selectedRadioButtonId == 2131230976 ){
            priority = 1
            count = 1
            binding.saveNoteButton.isEnabled = true
        }else if (selectedRadioButtonId == 2131231003){
            priority = 2
            count = 2
            binding.saveNoteButton.isEnabled = true
        }else{
            priority = 3
            count = 3
            binding.saveNoteButton.isEnabled = true
        }

        val db = Firebase.firestore
        val currentUser = auth.currentUser

        val previewText = if (noteText.length > 30) {  // İlk 50 karakteri al
            noteText.substring(0, 30)
        } else {
            noteText
        }

        if (currentUser != null) {
            val note = hashMapOf(
                "userId" to currentUser.uid,
                "text" to previewText,
                "fullText" to noteText,
                "priority" to priority,
                "isCompleted" to isCompleted,
                "documentId" to ""  //Document ID'sini aşağıda ekle.
            )

            db.collection(currentUser.email!!)
                .add(note)
                .addOnSuccessListener { documentReference ->
                    val newDocumentId = documentReference.id

                    val newNote = Note(
                        id = "",
                        userId = currentUser.uid,
                        text = previewText,
                        fullText = noteText,
                        priority = priority,
                        isCompleted = isCompleted,
                        documentId = newDocumentId
                    )

                    // Not silindikten sonra NotesListActivity'i yenile
                    val intent = Intent(this, NotesListActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    println("error")
                }
        }

    }

    // RadioButton tıklandığında çağrılacak fonksiyon
    fun RadioBtnClicked(view: View){
        val priorityRadioGroup = binding.priorityRadioGroup
        val selectedRadioButtonId = priorityRadioGroup.checkedRadioButtonId

        if (selectedRadioButtonId == 2131230976 ){
            count = 1
            binding.saveNoteButton.isEnabled = true
        }else if (selectedRadioButtonId == 2131231003){
            count = 2
            binding.saveNoteButton.isEnabled = true
        }else{
            count = 3
            binding.saveNoteButton.isEnabled = true
        }
    }
}
