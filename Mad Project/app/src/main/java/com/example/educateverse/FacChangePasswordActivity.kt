package com.example.educateverse

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FacChangePasswordActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var mFirebaseDatabaseInstances: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fac_change_password)
        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabaseInstances = FirebaseFirestore.getInstance()
        supportActionBar?.title="Change Password"

    }
    fun newpassword(v: View){
        var i= intent
        var username=i.getStringExtra("FID")
        var currentPass=(findViewById<View>(R.id.currentpass) as EditText).text.toString()
        var newPass=(findViewById<View>(R.id.newpass)as EditText).text.toString()
        var confirmpass=(findViewById<View>(R.id.confirmpass)as EditText).text.toString()

        try {
            mFirebaseDatabaseInstances?.collection("Faculty")!!.document(username.toString()).get()
                .addOnSuccessListener { result ->
                    val fac = result.toObject(Faculty::class.java)
                    if (fac == null) {
                       //Toast.makeText(this, "ERROR IN LOADING THE DATA", Toast.LENGTH_LONG).show()

                        //alert box code

                        val dialogBuilder = AlertDialog.Builder(this)

                        // set message of alert dialog
                        dialogBuilder.setMessage("Error In Loading The Data")
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
                        if (!(currentPass.equals(fac.password))) {
                            //Toast.makeText(this, " ERROR: CURRENT PASSWORD IS NOT CORRECT ", Toast.LENGTH_LONG).show()

                            //alert box code

                            val dialogBuilder = AlertDialog.Builder(this)

                            // set message of alert dialog
                            dialogBuilder.setMessage("Current Password Is Invalid")
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
                            if (newPass.equals(confirmpass)) {
                                changepass(fac.FID,newPass)
                            } else {
                                //Toast.makeText(this, " ERROR: NEW PASSWORD DOESN'T MATCH WITH THE CONFIRM PASSWORD ", Toast.LENGTH_LONG).show()

                                //alert box code

                                val dialogBuilder = AlertDialog.Builder(this)

                                // set message of alert dialog
                                dialogBuilder.setMessage("New Password Did Not Match With The Confirm Password")
                                    // positive button text and action
                                    .setPositiveButton("Ok", DialogInterface.OnClickListener {
                                            dialog, id ->
                                    })


                                // create dialog box
                                val alert = dialogBuilder.create()
                                // set title for alert dialog box
                                alert.setTitle("Password Error!")
                                // show alert dialog
                                alert.show()

                                //alert box code upto here

                            }
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
            alert.setTitle("Error")
            // show alert dialog
            alert.show()

            //alert box code upto here

        }
    }
    fun changepass(username:String,newpass:String) {
        mFirebaseDatabaseInstances?.collection("Faculty")!!.document(username.toString()).get()
            .addOnSuccessListener { result ->
                val fac = result.toObject(Faculty::class.java)
                if (fac == null) {
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
                    alert.setTitle("Error")
                    // show alert dialog
                    alert.show()

                    //alert box code upto here

                } else {

                    var u = Faculty(fac.name,
                        fac.honorific,
                        fac.branch,
                        fac.designation,
                        fac.FID,
                        fac.email,
                        fac.mobile,
                        fac.address,
                        newpass,
                        fac.image)
                    mFirebaseDatabaseInstances?.collection("Faculty")?.document(username!!)?.set(u)
                    //Toast.makeText(this, "PASSWORD CHANGED SUCESSFULLY", Toast.LENGTH_LONG).show()

                    //alert box code

                    val dialogBuilder = AlertDialog.Builder(this)

                    // set message of alert dialog
                    dialogBuilder.setMessage("Your Password has been Changed Successfully.")
                        // positive button text and action
                        .setPositiveButton("Ok", DialogInterface.OnClickListener {
                                dialog, id ->
                        })


                    // create dialog box
                    val alert = dialogBuilder.create()
                    // set title for alert dialog box
                    alert.setTitle("Password Changed!")
                    // show alert dialog
                    alert.show()

                    //alert box code upto here


                }
            }
    }
}