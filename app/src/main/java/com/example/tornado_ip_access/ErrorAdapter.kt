package com.example.tornado_ip_access

// ErrorAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ErrorAdapter(private var errorList: List<String>) : RecyclerView.Adapter<ErrorAdapter.ErrorViewHolder>() {

    class ErrorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val errorTextView: TextView = itemView.findViewById(R.id.errorTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ErrorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_error, parent, false)
        return ErrorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ErrorViewHolder, position: Int) {
        holder.errorTextView.text = errorList[position]
    }

    override fun getItemCount(): Int {
        return errorList.size
    }

    fun updateErrors(newErrors: List<String>) {
        errorList = newErrors
        notifyDataSetChanged()
    }
}
