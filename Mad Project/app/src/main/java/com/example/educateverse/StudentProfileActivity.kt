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

class StudentProfileActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private val PICK_IMAGE_REQUEST = 11
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    var url = ""
    private var storageReference: StorageReference? = null
    private var mFirebaseDatabaseInstances: FirebaseFirestore? = null
    var username = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_profile)
        mAuth = FirebaseAuth.getInstance()
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        var i = intent
        mFirebaseDatabaseInstances = FirebaseFirestore.getInstance()
        supportActionBar?.title = "Profile"
        loadData()
    }

    fun loadData() {

        var i = intent
        var usn = i.getStringExtra("usn")
        if (usn != null) {
            username = usn
        }
        try {
            mFirebaseDatabaseInstances?.collection("Student")!!.document(username).get()
                .addOnSuccessListener { result ->
                    val stud = result.toObject(Student::class.java)
                    if (stud == null) {
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
                        var usn = findViewById<View>(R.id.usn) as TextView
                        var year = findViewById<View>(R.id.year) as TextView
                        var section = findViewById<View>(R.id.section) as TextView
                        var email = findViewById<View>(R.id.email) as TextView
                        var mobile = findViewById<View>(R.id.mobile) as TextView
                        var Address = findViewById<View>(R.id.address) as TextView
                        var imageView = findViewById<View>(R.id.upphoto) as ImageView

                        name.text = stud.name
                        branch.text = stud.branch
                        usn.text = stud.usn
                        year.text = stud.year
                        email.text = stud.email
                        mobile.text = stud.mobile
                        Address.text = stud.address
                        section.text = stud.sec
                        if (stud.image != "") {
                            var url = URL(stud.image)

                            Glide.with(this@StudentProfileActivity).load(url)
                                .placeholder(R.drawable.person).error(R.drawable.person)
                                .override(80, 80).into(imageView)

                        }
                    }
                }
        } catch (e: Exception) {
            //Toast.makeText(this, "ERROR IN LOADING THE DATA", Toast.LENGTH_LONG).show()

            //alert box code

            val dialogBuilder = AlertDialog.Builder(this)

            // set message of alert dialog
            dialogBuilder.setMessage("Error in Loading the data.")
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

    fun upload(v: View?) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "SELECT PICTURE"), PICK_IMAGE_REQUEST)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }

            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                var ui = findViewById<View>(R.id.upphoto) as ImageView
                ui.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }


    }

    fun addUploadRecordToDb(uri: String) {
        //val db = FirebaseFirestore.getInstance()
        //  Toast.makeText(this,"inside..",Toast.LENGTH_LONG).show()
        mFirebaseDatabaseInstances = FirebaseFirestore.getInstance()
        val data = HashMap<String, Any>()




        url = uri
        try {
            mFirebaseDatabaseInstances?.collection("Student")!!.document(username).get()
                .addOnSuccessListener { result ->
                    val stud = result.toObject(Student::class.java)
                    if (stud == null) {
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

                        var student = Student(
                            stud.name,
                            stud.usn,
                            stud.branch,
                            stud.year,
                            stud.sec,
                            stud.email,
                            stud.mobile,
                            stud.address,
                            stud.password,
                            url,
                            stud.label1,
                            stud.label2
                        )
                        mFirebaseDatabaseInstances?.collection("Student")?.document(username!!)
                            ?.set(student)
                        //Toast.makeText(this, "PROFILE PICTURE UPDATED SUCESSFULLY", Toast.LENGTH_LONG).show()

                        //alert box code

                        val dialogBuilder = AlertDialog.Builder(this)

                        // set message of alert dialog
                        dialogBuilder.setMessage("Profile Updated Successfully.")
                            // positive button text and action
                            .setPositiveButton("Ok", DialogInterface.OnClickListener {
                                    dialog, id ->
                            })


                        // create dialog box
                        val alert = dialogBuilder.create()
                        // set title for alert dialog box
                        alert.setTitle("Success")
                        // show alert dialog
                        alert.show()

                        //alert box code upto here

                    }

                }
        } catch (e: Exception) {
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

    fun update(v: View?) {
        if (filePath != null) {
            val ref = storageReference?.child("uploads/" + UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(filePath!!)

            val urlTask =
                uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    ref.downloadUrl
                })?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        // Toast.makeText(this,downloadUri.toString(),Toast.LENGTH_LONG).show()
                        addUploadRecordToDb(downloadUri.toString())
                    } else {
                        // Handle failures
                    }
                }?.addOnFailureListener {

                }
        } else {
            //Toast.makeText(this, "IMAGE NOT UPLOADED", Toast.LENGTH_SHORT).show()

            //alert box code

            val dialogBuilder = AlertDialog.Builder(this)

            // set message of alert dialog
            dialogBuilder.setMessage("Image is not uploaded.")
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

    fun changepassword(v: View?) {
        var i = Intent(applicationContext, StudentChangePassword::class.java)
        i.putExtra("usn", username)
        startActivity(i)


    }
}

