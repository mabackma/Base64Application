package com.example.base64application

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.base64application.databinding.RecyclerviewMyPictureRowBinding
import com.example.base64application.datatypes.MyPicture

class MyPictureAdapter(private val pictures: List<MyPicture>) : RecyclerView.Adapter<MyPictureAdapter.MyPictureHolder>() {

    // binding layerin muuttujien alustaminen
    private var _binding: RecyclerviewMyPictureRowBinding? = null
    private val binding get() = _binding!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPictureHolder {
        _binding = RecyclerviewMyPictureRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyPictureHolder(binding)
    }

    override fun onBindViewHolder(holder: MyPictureHolder, position: Int) {
        val itemMyPicture = pictures[position]
        holder.bindMyPicture(itemMyPicture)
    }

    override fun getItemCount(): Int {
        return pictures.size
    }

    class MyPictureHolder(v: RecyclerviewMyPictureRowBinding) : RecyclerView.ViewHolder(v.root), View.OnClickListener {

        // tämän Itemin ulkoasu ja varsinainen data
        private var view: RecyclerviewMyPictureRowBinding = v
        private var picture: MyPicture? = null

        // mahdollistetaan yksittäisen itemin klikkaaminen tässä luokassa
        init {
            v.root.setOnClickListener(this)
        }

        // metodi, joka kytkee datan yksityiskohdat ulkoasun yksityiskohtiin
        fun bindMyPicture(picture : MyPicture)
        {
            this.picture = picture
            view.textViewTitle.text = picture.showTitle()
            view.textViewDescription.text = picture.toString()
        }

        // jos itemiä klikataan käyttöliittymässä, ajetaan tämä koodio
        override fun onClick(v: View) {

        }

    }
}