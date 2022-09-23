package com.example.educateverse

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AddFaculty : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var mFirebaseDatabaseInstances: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_faculty)
        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabaseInstances = FirebaseFirestore.getInstance()
        supportActionBar?.title="Add Faculty"
    }
    fun addFaculty(v: View?){
        val honor=findViewById<View>(R.id.honorif) as Spinner
        val honorific=honor.selectedItem.toString()
        val name = (findViewById<View?>(R.id.name) as EditText).text.toString().uppercase(Locale.getDefault())
        val branch=(findViewById<View>(R.id.branch)as Spinner).selectedItem.toString()
        val desig = findViewById<View>(R.id.designation) as Spinner
        val designation=desig.selectedItem.toString()
        val email = (findViewById<View>(R.id.email) as EditText).text.toString()
        val mobile = (findViewById<View>(R.id.mobile) as EditText).text.toString()
        val address = (findViewById<View>(R.id.address) as EditText).text.toString().uppercase(
            Locale.getDefault())
        val FID=(findViewById<View>(R.id.FID)as EditText).text.toString()
        var p1=""


        val docRef = mFirebaseDatabaseInstances?.collection("Faculty")?.document(FID!!)
        docRef?.get()?.addOnSuccessListener { documentSnapshot ->
            val user = documentSnapshot.toObject(Faculty::class.java) as Faculty?
            if (user != null)//User Already exists
            {
                //Toast.makeText(this, "ERROR: FACULTY RECORD EXIST", Toast.LENGTH_LONG).show()

                //alert box code

                val dialogBuilder = AlertDialog.Builder(this)

                // set message of alert dialog
                dialogBuilder.setMessage("FACULTY RECORD EXIST")
                    // if the dialog is cancelable
                    .setCancelable(false)
                    // positive button text and action
                    .setPositiveButton("Ok", DialogInterface.OnClickListener {
                            dialog, id -> finish()
                    })


                // create dialog box
                val alert = dialogBuilder.create()
                // set title for alert dialog box
                alert.setTitle("Error")
                // show alert dialog
                alert.show()

                //alert box code upto here

            } else {
                p1=mobile
                var u = Faculty(name, honorific, branch, designation,FID, email, mobile, address, p1)
                mFirebaseDatabaseInstances?.collection("Faculty")?.document(FID!!)?.set(u)
               // Toast.makeText(this, "SUCCESS: FACULTY ADDED", Toast.LENGTH_LONG).show()

                //alert box code

                val dialogBuilder = AlertDialog.Builder(this)

                // set message of alert dialog
                dialogBuilder.setMessage("Congrats! Faculty Added")
                    // if the dialog is cancelable
                    .setCancelable(false)
                    // positive button text and action
                    .setPositiveButton("Ok", DialogInterface.OnClickListener {
                            dialog, id -> finish()
                    })


                // create dialog box
                val alert = dialogBuilder.create()
                // set title for alert dialog box
                alert.setTitle("SUCCESS")
                // show alert dialog
                alert.show()

                //alert box code upto here

            }
        }

    }

}
class Faculty {
    var name=""
    var honorific=""
    var branch=""
    var designation=""
    var FID=""

    var email=""
    var mobile=""
    var address=""
    var password=""
    var image=""
    var faclable1=""
    var faclable2=""

    constructor(name:String,honorific:String,branch:String,designation:String,FID:String,email:String,mobile:String,address:String,password:String){
        this.name=name
        this.honorific=honorific
        this.branch=branch
        this.designation=designation
        this.FID=FID

        this.email=email
        this.mobile=mobile
        this.address=address
        this.password=password
        this.image=""
        this.faclable1=""
        this.faclable2=""
    }
    constructor(name:String,honorific:String,branch:String,designation:String,FID:String,email:String,mobile:String,address:String,password:String,image:String){
        this.name=name
        this.honorific=honorific
        this.branch=branch
        this.designation=designation
        this.FID=FID

        this.email=email
        this.mobile=mobile
        this.address=address
        this.password=password
        this.image=image
        this.faclable1=""
        this.faclable2=""
    }
    constructor(name:String,honorific:String,branch:String,designation:String,FID:String,email:String,mobile:String,address:String,password:String,image:String,label1:String,label2:String){
        this.name=name
        this.honorific=honorific
        this.branch=branch
        this.designation=designation
        this.FID=FID

        this.email=email
        this.mobile=mobile
        this.address=address
        this.password=password
        this.image=image
        this.faclable1=label1
        this.faclable2=label2
    }
    constructor()


}