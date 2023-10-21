package com.mahsunsayak.todolist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mahsunsayak.todolist.R
import com.mahsunsayak.todolist.model.Note

// Notları göstermek için kullanılan RecyclerView için adaptör sınıfı
class NoteAdapter(var notes: List<Note>) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private var onItemClickListener: ((Note) -> Unit)? = null
    private var onCheckedChangeListener: ((Note, Boolean) -> Unit)? = null

    // Tıklama olayını dinlemek için bir listener ayarla
    fun setOnItemClickListener(listener: (Note) -> Unit) {
        onItemClickListener = listener
    }

    // CheckBox değişikliklerini dinlemek için bir listener ayarla
    fun setOnCheckedChangeListener(listener: (Note, Boolean) -> Unit) {
        onCheckedChangeListener = listener
    }

    // Not öğesinin görünümünü tutan inner sınıf
    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.noteText)
        val priority: TextView = itemView.findViewById(R.id.notePriority)
        val documentText: TextView = itemView.findViewById(R.id.documentText)
        val checkBox: CheckBox = itemView.findViewById(R.id.noteCheckBox)
    }

    // Yeni bir ViewHolder oluştur
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(itemView)
    }

    // ViewHolder'a verileri bağla
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentItem = notes[position]
        holder.text.text = currentItem.text
        holder.priority.text = currentItem.priority.toString()
        holder.documentText.text = currentItem.documentId

        // Öncelik değerini oku ve düzeltilmiş metinle güncelle
        if (holder.priority.text == "1" ){
            holder.priority.text = "Low"
        } else if (holder.priority.text == "2"){
            holder.priority.text = "Medium"
        } else{
            holder.priority.text = "High"
        }

        // Öğeye tıklama olayını ekle
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(currentItem)
        }

        // Notun durumuna göre checkbox'un işaretini ayarla
        holder.checkBox.isChecked = currentItem.isCompleted

        // CheckBox değişikliğini dinle ve listener'a iletilmesini sağla
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (currentItem.isCompleted != isChecked) {
                onCheckedChangeListener?.invoke(currentItem, isChecked)
            }
        }
    }

    // Liste öğelerinin sayısını döndür
    override fun getItemCount() = notes.size
}
