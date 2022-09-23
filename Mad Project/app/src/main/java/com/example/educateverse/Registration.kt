package com.example.educateverse

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.IOException
import java.util.*

class Registration : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var mFirebaseDatabaseInstances: FirebaseFirestore?=null
    var url=""
    override fun onCreate(savedInstanceState: Bundle?) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        //fullscreen


        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        var i=intent
        mFirebaseDatabaseInstances = FirebaseFirestore.getInstance()
    }

    fun validate(v: View?) {
        val name = (findViewById<View?>(R.id.name) as EditText).text.toString().uppercase(Locale.getDefault())
        val usn = (findViewById<View>(R.id.usn) as EditText).text.toString().uppercase(Locale.getDefault())
        val b = findViewById<View>(R.id.branch) as Spinner
        val branch = b.selectedItem.toString()
        val s = findViewById<View>(R.id.year) as Spinner
        val year = s.selectedItem.toString()
        val se = findViewById<View>(R.id.section) as Spinner
        val sec = se.selectedItem.toString()
        val email = (findViewById<View>(R.id.email) as EditText).text.toString()
        val mobile = (findViewById<View>(R.id.mobile) as EditText).text.toString()
        val address = (findViewById<View>(R.id.address) as EditText).text.toString().uppercase(
            Locale.getDefault())
        val p1 = (findViewById<View>(R.id.pass) as EditText).text.toString()
        val p2 = (findViewById<View>(R.id.conf_pass) as EditText).text.toString()

        if (p1.equals(p2)) {
            val docRef = mFirebaseDatabaseInstances?.collection("StudentReq")?.document(usn!!)
            docRef?.get()?.addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(Student::class.java) as Student?
                if (user != null)//User Already exists
                {
                    //Toast.makeText(this, "ERROR: STUDENT RECORD EXIST", Toast.LENGTH_LONG).show()

                    //alert box code

                    val dialogBuilder = AlertDialog.Builder(this)

                    // set message of alert dialog
                    dialogBuilder.setMessage("Student record  exist.")
                        // positive button text and action
                        .setPositiveButton("Ok", DialogInterface.OnClickListener {
                                dialog, id ->
                        })


                    // create dialog box
                    val alert = dialogBuilder.create()
                    // set title for alert dialog box
                    alert.setTitle("Record Error")
                    // show alert dialog
                    alert.show()

                    //alert box code upto here


                } else {
                    var u = Student(name, usn, branch, year, sec, email, mobile, address, p1,url)
                    mFirebaseDatabaseInstances?.collection("StudentReq")?.document(usn!!)?.set(u)
                    //Toast.makeText(this, " SUCCESS: REQUEST ACCEPTED ", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, AccountCreated::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    this.startActivity(intent)
                }
            }
        }

        else{
            //Toast.makeText(this, "ERROR: PASSWORD AUTHENTICATION FAILED", Toast.LENGTH_LONG).show()

            //alert box code

            val dialogBuilder = AlertDialog.Builder(this)

            // set message of alert dialog
            dialogBuilder.setMessage("Password Authentication Failed.")
                // positive button text and action
                .setPositiveButton("Ok", DialogInterface.OnClickListener {
                        dialog, id ->
                })


            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Password Authentication")
            // show alert dialog
            alert.show()

            //alert box code upto here

        }
    }
    fun upload(v:View?){
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
    fun addUploadRecordToDb(uri: String){
        //val db = FirebaseFirestore.getInstance()
        //  Toast.makeText(this,"inside..",Toast.LENGTH_LONG).show()
        mFirebaseDatabaseInstances= FirebaseFirestore.getInstance()
        val data = HashMap<String, Any>()




        url = uri
        //Toast.makeText(this,"{$pid,$pname,$cost,$url}",Toast.LENGTH_LONG).show()
    }

}

class Student {
    var name = ""
    var usn = ""
    var branch = ""
    var year = ""
    var sec = ""

    var email = ""
    var mobile = ""
    var address = ""
    var password = ""
    var image = ""
    var label1 = ""
    var label2 = ""

    constructor(
        name: String,
        usn: String,
        branch: String,
        year: String,
        sec: String,
        email: String,
        mobile: String,
        address: String,
        password: String,
        url: String,
        label1: String,
        label2: String
    ) {
        this.name = name
        this.usn = usn
        this.branch = branch
        this.year = year
        this.sec = sec

        this.email = email
        this.mobile = mobile
        this.address = address
        this.password = password
        this.image = url
        this.label1 = label1
        this.label2 = label2
    }

    constructor(
        name: String,
        usn: String,
        branch: String,
        year: String,
        sec: String,
        email: String,
        mobile: String,
        address: String,
        password: String,
        url: String
    ) {
        this.name = name
        this.usn = usn
        this.branch = branch
        this.year = year
        this.sec = sec

        this.email = email
        this.mobile = mobile
        this.address = address
        this.password = password
        this.image = url
        this.label1 = ""
        this.label2 = ""
    }

    constructor()


}