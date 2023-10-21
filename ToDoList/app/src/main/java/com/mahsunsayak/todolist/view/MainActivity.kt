package com.mahsunsayak.todolist.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mahsunsayak.todolist.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // View bağlantıları ve Firebase yetkilendirmesi için gerekli değişkenler
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Activity için bağlantıları oluştur
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Firebase Auth'u başlat
        auth = Firebase.auth

        // Eğer kullanıcı zaten oturum açmışsa, Notlar Listesi ekranına yönlendir
        val currentUser = auth.currentUser
        if (currentUser != null){
            val intent = Intent(this@MainActivity, NotesListActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Giriş butonuna tıklandığında çağrılan fonksiyon
    fun signInClicked (view : View) {
        val email = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()

        // Eğer email veya şifre boşsa kullanıcıya uyarı göster
        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this@MainActivity,"Enter email and password!", Toast.LENGTH_LONG).show()
        } else {
            // Kullanıcı girişi başarılıysa Notlar Listesi ekranına yönlendir
            auth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener {
                val intent = Intent(this@MainActivity, NotesListActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                // Giriş başarısız olduğunda hata mesajını göster
                Toast.makeText(this@MainActivity,it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    // Kayıt butonuna tıklandığında çağrılan fonksiyon
    fun signUpClicked (view : View) {
        val email = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()

        // Eğer email veya şifre boşsa kullanıcıya uyarı göster
        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this@MainActivity,"Enter email and password!", Toast.LENGTH_LONG).show()
        } else {
            // Kullanıcı kaydı başarılıysa Notlar Listesi ekranına yönlendir
            auth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener {
                val intent = Intent(this@MainActivity, NotesListActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                // Kayıt başarısız olduğunda hata mesajını göster
                Toast.makeText(this@MainActivity,it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }
}
