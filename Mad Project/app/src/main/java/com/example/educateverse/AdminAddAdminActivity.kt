package com.example.educateverse

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AdminAddAdminActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var mFirebaseDatabaseInstances: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_admin)
        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabaseInstances = FirebaseFirestore.getInstance()
        supportActionBar?.title="Add Admin"
    }
    fun addAdmin(v: View?){
        var username= (findViewById<View>(R.id.username)as EditText).text.toString().uppercase(
            Locale.getDefault())
        var password=(findViewById<View>(R.id.pass)as EditText).text.toString()
        var confPass=(findViewById<View>(R.id.confirmpass)as EditText).text.toString()
        try {
            mFirebaseDatabaseInstances?.collection("Admin")?.document(username)?.get()
                ?.addOnSuccessListener { result ->
                    val admin = result.toObject(Admin::class.java)
                    if (admin != null) {
                        //Toast.makeText(this, "ERROR: ADMIN RECORD EXISTS", Toast.LENGTH_LONG).show()

                        //alert box code

                        val dialogBuilder = AlertDialog.Builder(this)

                        // set message of alert dialog
                        dialogBuilder.setMessage("Admin Record Exists")
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
                        if (!(password.equals(confPass))) {
                            Toast.makeText(this, "PASSWORD Did Not Match.", Toast.LENGTH_LONG).show()

                            //alert box code

                            val dialogBuilder = AlertDialog.Builder(this)

                            // set message of alert dialog
                            dialogBuilder.setMessage("Password Did Not Match")
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
                            val ad=Admin(username,password)
                            mFirebaseDatabaseInstances?.collection("Admin")?.document(username!!)?.set(ad)
                            //Toast.makeText(this, "ADMIN ADDED SUCESSFULLY", Toast.LENGTH_LONG).show()

                            //alert box code

                            val dialogBuilder = AlertDialog.Builder(this)

                            // set message of alert dialog
                            dialogBuilder.setMessage("Congrats! Admin Added")
                                // positive button text and action
                                .setPositiveButton("Ok", DialogInterface.OnClickListener {
                                        dialog, id ->
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
        catch(e:Exception)
        {
            //Toast.makeText(this, "UNEXPECTED ERROR", Toast.LENGTH_LONG).show()
            //alert box code

            val dialogBuilder = AlertDialog.Builder(this)

            // set message of alert dialog
            dialogBuilder.setMessage("Unexpected Error")
                // positive button text and action
                .setPositiveButton("Ok", DialogInterface.OnClickListener {
                        dialog, id ->
                })


            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("ERROR")
            // show alert dialog
            alert.show()

            //alert box code upto here
        }

    }
}
class Admin{
    var username=""
    var password=""
    constructor(username:String,password:String){
        this.username=username
        this.password=password
    }
    constructor()
}