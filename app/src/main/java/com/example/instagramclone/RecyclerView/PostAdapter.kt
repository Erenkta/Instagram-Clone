package com.example.instagramclone.RecyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.Model.PostModel
import com.example.instagramclone.R
import com.example.instagramclone.databinding.RecyclerItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.time.temporal.ChronoUnit

class PostAdapter(val postArray:ArrayList<PostModel>) : RecyclerView.Adapter<ViewHolder> (){
    val currentUser = FirebaseAuth.getInstance().currentUser
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.userName.text = postArray.get(position).userEmail
        holder.binding.userComment.text = postArray.get(position).comment
        Picasso.get().load(postArray.get(position).downloadUrl).into(holder.binding.postImage)








    }
    override fun getItemCount(): Int {
        return  postArray.size
    }





}