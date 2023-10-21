package com.mahsunsayak.todolist.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mahsunsayak.todolist.databinding.ActivityNoteDetailBinding

class NoteDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteDetailBinding
    private lateinit var auth: FirebaseAuth

    // Seçilen notun benzersiz kimliği
    private var selectedNoteId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Firebase authentication nesnesini başlat
        auth = Firebase.auth

        // Intent'ten seçilen notun kimliğini al, eğer null ise boş bir string ata
        selectedNoteId = intent.getStringExtra("NOTE_ID") ?: ""

        // Intent'ten notun metnini al, eğer null ise boş bir string ata ve ekranda göster
        val noteText = intent.getStringExtra("NOTE_TEXT") ?: ""
        binding.noteDetailText.text = noteText

        // "Sil" butonuna tıklandığında deleteNoteClicked() fonksiyonunu çağır
        binding.deleteNoteButton.setOnClickListener {
            deleteNoteClicked()
        }
    }

    // Notu silme işlemi gerçekleştirildiğinde çağrılan fonksiyon
    private fun deleteNoteClicked() {
        val db = Firebase.firestore
        val currentUser = auth.currentUser

        selectedNoteId?.let {
            db.collection(currentUser!!.email!!)
                .document(selectedNoteId!!)
                .delete()
                .addOnSuccessListener {

                    // Not silindikten sonra NotesListActivity'i yenile
                    val intent = Intent(this, NotesListActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {

                    // Hata durumunda kullanıcıya bir bildirim göster
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
        }
    }
}
