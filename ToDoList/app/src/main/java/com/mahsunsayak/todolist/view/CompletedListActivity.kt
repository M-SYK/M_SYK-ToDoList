package com.mahsunsayak.todolist.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mahsunsayak.todolist.adapter.NoteAdapter
import com.mahsunsayak.todolist.databinding.ActivityCompletedListBinding
import com.mahsunsayak.todolist.model.Note
import java.util.UUID

class CompletedListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompletedListBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompletedListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        recyclerView = binding.notesRecyclerView
        swipeRefreshLayout = binding.swipeRefreshLayout
        recyclerView.layoutManager = LinearLayoutManager(this)
        noteAdapter = NoteAdapter(emptyList())
        recyclerView.adapter = noteAdapter

        // İntent'ten gelen not metnini kontrol et ve yeni bir tamamlanmış not ekleyin
        val noteText = intent.getStringExtra("NOTE_TEXT")
        if (noteText != null) {
            val newNote = Note(
                id = UUID.randomUUID().toString(),
                text = noteText,
                fullText = "",
                isCompleted = true
            )
            noteAdapter.notes = listOf(newNote) + noteAdapter.notes
            noteAdapter.notifyDataSetChanged()
        }

        // Tamamlanan notları yükle
        loadCompletedNotes()

        // Not işaretleme (check) durumunu dinleyen bir fonksiyon ekleyin
        noteAdapter.setOnCheckedChangeListener { note, isChecked ->
            if (isChecked) {
                val db = Firebase.firestore
                val currentUser = auth.currentUser

                // Notu tamamlandı olarak işaretle ve güncelle
                db.collection(currentUser!!.email!!)
                    .document(note.documentId)
                    .update("isCompleted", false)
                    .addOnSuccessListener {
                        loadCompletedNotes() // Notları tekrar yükle
                        val intent = Intent(this, NotesListActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        // Hata mesajını göster
                    }
            }
        }

        // Nota tıklama durumunu dinleyen bir fonksiyon ekle
        noteAdapter.setOnItemClickListener { note ->
            showNoteDetails(note)
        }
    }

    // Tamamlanmış notları yükleyen fonksiyon
    private fun loadCompletedNotes() {
        val db = Firebase.firestore
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val query = db.collection(Firebase.auth.currentUser!!.email!!)
                .whereEqualTo("isCompleted", true)

            query.get()
                .addOnSuccessListener { result ->
                    val notesList = mutableListOf<Note>()
                    for (document in result) {
                        val note = document.toObject(Note::class.java)

                        if (note.documentId.isEmpty()) {
                            // Belge ID'si henüz eklenmemişse, notu güncelle
                            val newNote = note.copy(documentId = document.id)
                            notesList.add(newNote)
                        } else {
                            notesList.add(note)
                        }
                    }
                    // Notları öncelik sırasına göre sırala
                    val sortedNotesList = notesList.sortedByDescending { it.priority }
                    noteAdapter.notes = sortedNotesList
                    noteAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    // Hata mesajını göster
                }
                .addOnCompleteListener {
                    // Yenileme işlemi tamamlandığında yenileme animasyonunu durdur
                    swipeRefreshLayout.isRefreshing = false
                }
        }
    }

    // Not detaylarını gösteren bir aktiviteyi başlatan fonksiyon
    private fun showNoteDetails(note: Note) {
        val intent = Intent(this, NoteDetailActivity::class.java)
        intent.putExtra("NOTE_ID", note.documentId)
        intent.putExtra("NOTE_TEXT", note.fullText)
        startActivity(intent)
        finish()
    }
}
