package com.example.educateverse

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.IOException
import java.net.URL
import java.util.*

class FacultyProfileActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    var url=""
    private var storageReference: StorageReference? = null
    private var mFirebaseDatabaseInstances: FirebaseFirestore? = null
    var username=""
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faculty_profile)
        mAuth = FirebaseAuth.getInstance()
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        var i=intent
        mFirebaseDatabaseInstances = FirebaseFirestore.getInstance()
        supportActionBar?.title="Profile"

        loadData()
    }

    fun loadData(){

        var i= intent
        var FacID=i.getStringExtra("FID")
        if (FacID != null) {
            username=FacID
        }
        try {
            mFirebaseDatabaseInstances?.collection("Faculty")!!.document(username.toString()).get()
                .addOnSuccessListener { result ->
                    val fac = result.toObject(Faculty::class.java)
                    if (fac == null) {
                        //Toast.makeText(this, "ERROR IN LOADING THE DATA", Toast.LENGTH_LONG).show()

                        //alert box code

                        val dialogBuilder = AlertDialog.Builder(this)

                        // set message of alert dialog
                        dialogBuilder.setMessage("Error in loading the data.")
                            // positive button text and action
                            .setPositiveButton("Ok", DialogInterface.OnClickListener {
                                    dialog, id ->
                            })


                        // create dialog box
                        val alert = dialogBuilder.create()
                        // set title for alert dialog box
                        alert.setTitle("Error")
                        // show alert dialog
                        alert.show()

                        //alert box code upto here

                    } else {
                        var name = findViewById<View>(R.id.name) as TextView
                        var branch = findViewById<View>(R.id.branch) as TextView
                        var designation = findViewById<View>(R.id.designation) as TextView
                        var FID = findViewById<View>(R.id.FID) as TextView
                        var email = findViewById<View>(R.id.email) as TextView
                        var mobile = findViewById<View>(R.id.mobile) as TextView
                        var Address = findViewById<View>(R.id.address) as TextView
                        var imageView = findViewById<View>(R.id.upphoto) as ImageView

                        name.text = "${fac.honorific} ${fac.name}"
                        branch.text = fac.branch
                        designation.text = fac.designation
                        FID.text = fac.FID
                        email.text = fac.email
                        mobile.text = fac.mobile
                        Address.text = fac.address
                        if (fac.image != "") {
                            var url = URL(fac.image)

                            Glide.with(this@FacultyProfileActivity).load(url)
                                .placeholder(R.drawable.person).error(R.drawable.person)
                                .override(80, 80).into(imageView)

                        }
                    }
                }
        }
        catch(e:Exception)
        {
            //Toast.makeText(this, "ERROR IN LOADING THE DATA", Toast.LENGTH_LONG).show()

            //alert box code

            val dialogBuilder = AlertDialog.Builder(this)

            // set message of alert dialog
            dialogBuilder.setMessage("Error in loading the data.")
                // positive button text and action
                .setPositiveButton("Ok", DialogInterface.OnClickListener {
                        dialog, id ->
                })


            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Error")
            // show alert dialog
            alert.show()

            //alert box code upto here

        }
    }

    fun upload(v: View?){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "SELECT PICTURE"), PICK_IMAGE_REQUEST)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                var ui=findViewById<View>(R.id.upphoto) as ImageView
                ui.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }


    }
    fun addUploadRecordToDb(uri: String){
        //val db = FirebaseFirestore.getInstance()
        //  Toast.makeText(this,"inside..",Toast.LENGTH_LONG).show()
        mFirebaseDatabaseInstances= FirebaseFirestore.getInstance()
        val data = HashMap<String, Any>()




        url = uri
        try{
            mFirebaseDatabaseInstances?.collection("Faculty")!!.document(username.toString()).get().addOnSuccessListener {
                    result ->
                val fac=result.toObject(Faculty::class.java)
                if(fac==null){
                    //Toast.makeText(this, "ERROR IN LOADING THE DATA", Toast.LENGTH_LONG).show()

                    //alert box code

                    val dialogBuilder = AlertDialog.Builder(this)

                    // set message of alert dialog
                    dialogBuilder.setMessage("Error in loading the data.")
                        // positive button text and action
                        .setPositiveButton("Ok", DialogInterface.OnClickListener {
                                dialog, id ->
                        })


                    // create dialog box
                    val alert = dialogBuilder.create()
                    // set title for alert dialog box
                    alert.setTitle("Error")
                    // show alert dialog
                    alert.show()

                    //alert box code upto here

                }
                else{

                    var u = Faculty(fac.name, fac.honorific, fac.branch, fac.designation,fac.FID, fac.email, fac.mobile, fac.address, fac.password,url)
                    mFirebaseDatabaseInstances?.collection("Faculty")?.document(username!!)?.set(u)
                    //Toast.makeText(this, "PROFILE PICTURE UPDATED SUCESSFULLY", Toast.LENGTH_LONG).show()

                    //alert box code

                    val dialogBuilder = AlertDialog.Builder(this)

                    // set message of alert dialog
                    dialogBuilder.setMessage("Profile picture updated successfully.")
                        // positive button text and action
                        .setPositiveButton("Ok", DialogInterface.OnClickListener {
                                dialog, id ->
                        })


                    // create dialog box
                    val alert = dialogBuilder.create()
                    // set title for alert dialog box
                    alert.setTitle("Upload Success")
                    // show alert dialog
                    alert.show()

                    //alert box code upto here


                }
                //Toast.makeText(this,"{$pid,$pname,$cost,$url}",Toast.LENGTH_LONG).show()


            }
        }
        catch(e:Exception)
        {
            //Toast.makeText(this, "ERROR IN LOADING THE DATA", Toast.LENGTH_LONG).show()

            //alert box code

            val dialogBuilder = AlertDialog.Builder(this)

            // set message of alert dialog
            dialogBuilder.setMessage("Error in loading the data.")
                // positive button text and action
                .setPositiveButton("Ok", DialogInterface.OnClickListener {
                        dialog, id ->
                })


            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Error")
            // show alert dialog
            alert.show()

            //alert box code upto here

        }


    }
    fun update(v: View?){
        if(filePath != null){
            val ref = storageReference?.child("uploads/" + UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(filePath!!)

            val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            })?.addOnCompleteListener { task->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    // Toast.makeText(this,downloadUri.toString(),Toast.LENGTH_LONG).show()
                    addUploadRecordToDb(downloadUri.toString())
                } else {
                    // Handle failures
                }
            }?.addOnFailureListener{

            }
        }else{
            Toast.makeText(this, "IMAGE NOT UPLOADED", Toast.LENGTH_SHORT).show()

            //alert box code

            val dialogBuilder = AlertDialog.Builder(this)

            // set message of alert dialog
            dialogBuilder.setMessage("Image not uploaded.")
                // positive button text and action
                .setPositiveButton("Ok", DialogInterface.OnClickListener {
                        dialog, id ->
                })


            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Upload Error")
            // show alert dialog
            alert.show()

            //alert box code upto here

        }
    }
    fun changepassword(v: View?){
        var i = Intent(applicationContext,FacChangePasswordActivity::class.java)
        i.putExtra("FID",username)
        startActivity(i)

    }

}