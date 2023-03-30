package com.example.instagramclone.View

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore.Images.Media
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.instagramclone.databinding.ActivityPostBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class PostActivity : AppCompatActivity() {
    lateinit var binding:ActivityPostBinding
    private lateinit var activityResultLauncher:ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher:ActivityResultLauncher<String>
    var imageUri:Uri? = null

    //Firebase
    private lateinit var storage:FirebaseStorage
    private lateinit var auth:FirebaseAuth
    private lateinit var firestore:FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPostBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //Initialize
        auth = Firebase.auth
        storage = Firebase.storage
        firestore = Firebase.firestore
        //
        registerLauncher()
        binding.selectImage.setOnClickListener { view->
            selectImage(view)
        }
        binding.buttonSave.setOnClickListener { view->
            upload(view)

        }
    }

    private fun upload(view: View) {
        //Fotoyu ve comment'i aktarma

        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg" //Random isim oluşturduk

        val reference = storage.reference
        val imageReference = reference.child("images/").child(imageName)
        if(imageUri != null){
            imageReference.putFile(imageUri!!).addOnSuccessListener {
                val uploadPictureReference = storage.reference.child("images/").child(imageName)
                uploadPictureReference.downloadUrl.addOnSuccessListener { //yukarıda dosyanın yoluna referans gösterdik şimdi de url i alcaz
                    val downloadUrl = it.toString() //Url
                    var id:Int = 1
                    //Veri tabanına fotoyu yorumu ve tarihi yazma
                    val postMap = hashMapOf<String,Any>("downloadUrl" to downloadUrl
                        ,"userEmail" to auth.currentUser!!.email!!
                        ,"comment" to binding.postComment.text.toString()
                        ,"date" to com.google.firebase.Timestamp.now())


                    firestore.collection("Posts")
                        .add(postMap)
                        .addOnSuccessListener {
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@PostActivity,it.localizedMessage,Toast.LENGTH_LONG).show() }

                }

            }.addOnFailureListener{
                Toast.makeText(this@PostActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun selectImage(view: View) {
        if(ContextCompat.checkSelfPermission(this@PostActivity,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //izin verilmediyse
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission need for the gallery",Snackbar.LENGTH_INDEFINITE)
                    .setAction("Give Permission"){
                        //intent to gallery
                        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                    .show()
            }
            else
            {
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        else{
            //izin verildiyse
            val intentToGallery = Intent(Intent.ACTION_PICK,Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)

        }
    }

    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
            if(result.resultCode == RESULT_OK){
                val intentFromResults = result.data
                if(intentFromResults != null){
                    imageUri = intentFromResults.data
                    imageUri.let{
                        binding.selectImage.setImageURI(imageUri)
                    }
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){result->
            if(result){
                val intentToGallery = Intent(Intent.ACTION_PICK,Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
            else{
                Toast.makeText(this@PostActivity,"Permission Needed!",Toast.LENGTH_LONG).show()
            }

        }
    }


}