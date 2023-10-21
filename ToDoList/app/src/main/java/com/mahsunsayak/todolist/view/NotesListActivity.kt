package com.mahsunsayak.todolist.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mahsunsayak.todolist.R
import com.mahsunsayak.todolist.adapter.NoteAdapter
import com.mahsunsayak.todolist.databinding.ActivityNotesListBinding
import com.mahsunsayak.todolist.model.Note

class NotesListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotesListBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        recyclerView = binding.notesRecyclerView
        swipeRefreshLayout = binding.swipeRefreshLayout
        recyclerView.layoutManager = LinearLayoutManager(this)
        noteAdapter = NoteAdapter(emptyList())
        recyclerView.adapter = noteAdapter

        // Yenileme işlemi başladığında çalışacak kod parçası
        swipeRefreshLayout.setOnRefreshListener {
            loadNotes()
        }

        loadNotes()

        // CheckBox işaretleme durumunda çalışacak kod parçası
        noteAdapter.setOnCheckedChangeListener { note, isChecked ->
            if (isChecked) {
                val db = Firebase.firestore
                val currentUser = auth.currentUser

                // Notu tamamlandı olarak işaretle
                db.collection(currentUser!!.email!!)
                    .document(note.documentId)
                    .update("isCompleted", true)
                    .addOnSuccessListener {
                        loadNotes() // Notları tekrar yükle
                    }
                    .addOnFailureListener { exception ->
                        // Hata mesajını göster
                    }
            }
        }

        // Nota tıklanınca çalışacak kod parçası
        noteAdapter.setOnItemClickListener { note ->
            showNoteDetails(note)
        }
    }

    // Notları yükleme işlemi
    private fun loadNotes() {
        val db = Firebase.firestore
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val query = db.collection(Firebase.auth.currentUser!!.email!!)
                .whereEqualTo("isCompleted", false)

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

    // Not detaylarını gösterme işlemi
    private fun showNoteDetails(note: Note) {
        val intent = Intent(this@NotesListActivity, NoteDetailActivity::class.java)
        intent.putExtra("NOTE_ID", note.documentId)
        intent.putExtra("NOTE_TEXT", note.fullText)
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_note) {
            val intent = Intent(this@NotesListActivity, AddNotesActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.sign_out) {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.go_to_completed_list) {
            val intent = Intent(this@NotesListActivity, CompletedListActivity::class.java)
            startActivity(intent)
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }
}
