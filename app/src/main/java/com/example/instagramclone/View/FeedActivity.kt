package com.example.instagramclone.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instagramclone.Model.PostModel
import com.example.instagramclone.R
import com.example.instagramclone.RecyclerView.PostAdapter
import com.example.instagramclone.databinding.ActivityFeedBinding
import com.example.instagramclone.databinding.RecyclerItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FeedActivity : AppCompatActivity() {
    lateinit var binding:ActivityFeedBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var intentToActivity:Intent
    //Verileri koymak için array
    private lateinit var postArray:ArrayList<PostModel>

    //Database
    private lateinit var db:FirebaseFirestore

    //Adapter
    private lateinit var postAdapter:PostAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityFeedBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //Firebase initialize
        auth = Firebase.auth
        db = Firebase.firestore
        //
        //Verilerimizi çekeceğimiz fonksiyon
        postArray = ArrayList<PostModel>()
        getDataFromFirebase()

        //RecyclerAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        postAdapter = PostAdapter(postArray)
        binding.recyclerView.adapter = postAdapter



    }



    private fun getDataFromFirebase() {
        //Verileri çekme işlemi

        db.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { value, error -> //FİLTERELEMEYİ WHERE İLE YAPICAKSIN MESELA KENDİ PROFİLİN İÇİN FİLTERELEME
            if(error != null){
                Toast.makeText(this@FeedActivity,error.localizedMessage, Toast.LENGTH_LONG).show()
            }
            else{
                if(value != null){ //value değeri yani bize bir value dönüyor olabilir fakat içi boş olma ihtimali de var
                    if(!value.isEmpty){
                        postArray.clear()
                       val documents =  value.documents
                        for(document in documents){
                            val comment = document.get("comment") as String     //Casting in kotlin
                            val userEmail = document.get("userEmail") as String //Casting in kotlin
                            val downloadUrl = document.get("downloadUrl") as String //Casting in kotlin
                            val post = PostModel(comment,userEmail,downloadUrl)
                            postArray.add(post)
                        }
                        postAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = getMenuInflater()
        menuInflater.inflate(R.menu.option_menu,menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.add_post){
            intentToActivity = Intent(this@FeedActivity, PostActivity::class.java)
            startActivity(intentToActivity)
        }
        else if(item.itemId == R.id.logout){
            auth.signOut()
            intentToActivity = Intent(this@FeedActivity, MainActivity::class.java)
            startActivity(intentToActivity)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}