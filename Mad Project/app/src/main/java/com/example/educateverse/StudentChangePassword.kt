package com.example.educateverse

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StudentChangePassword : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var mFirebaseDatabaseInstances: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_change_password)
        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabaseInstances = FirebaseFirestore.getInstance()
        supportActionBar?.title="Change Password"
    }
    fun newpassword(v: View){
        var i= intent
        var username=i.getStringExtra("usn")
        var currentPass=(findViewById<View>(R.id.currentpass) as EditText).text.toString()
        var newPass=(findViewById<View>(R.id.newpass)as EditText).text.toString()
        var confirmpass=(findViewById<View>(R.id.confirmpass)as EditText).text.toString()

        try {
            if (username != null) {
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
                            alert.setTitle("Loading Error")
                            // show alert dialog
                            alert.show()

                            //alert box code upto here

                        } else {
                            if (!(currentPass.equals(stud.password))) {
                                //Toast.makeText(this, " ERROR: CURRENT PASSWORD IS NOT CORRECT ", Toast.LENGTH_LONG).show()

                                //alert box code

                                val dialogBuilder = AlertDialog.Builder(this)

                                // set message of alert dialog
                                dialogBuilder.setMessage("Current password is invalid.")
                                    // positive button text and action
                                    .setPositiveButton("Ok", DialogInterface.OnClickListener {
                                            dialog, id ->
                                    })


                                // create dialog box
                                val alert = dialogBuilder.create()
                                // set title for alert dialog box
                                alert.setTitle("Password Error")
                                // show alert dialog
                                alert.show()

                                //alert box code upto here

                            } else {
                                if (newPass.equals(confirmpass)) {
                                    changepass(stud.usn,newPass)
                                } else {
                                    //Toast.makeText(this, " ERROR: NEW PASSWORD DOESN'T MATCH WITH THE CONFIRM PASSWORD ",Toast.LENGTH_LONG).show()

                                    //alert box code

                                    val dialogBuilder = AlertDialog.Builder(this)

                                    // set message of alert dialog
                                    dialogBuilder.setMessage("New password doesn't match with confirm password.")
                                        // positive button text and action
                                        .setPositiveButton("Ok", DialogInterface.OnClickListener {
                                                dialog, id ->
                                        })


                                    // create dialog box
                                    val alert = dialogBuilder.create()
                                    // set title for alert dialog box
                                    alert.setTitle("Password Error")
                                    // show alert dialog
                                    alert.show()

                                    //alert box code upto here

                                }
                            }
                        }
                    }
            }

        }
        catch(e:Exception)
        {
            Toast.makeText(this, "UNEXPECTED ERROR", Toast.LENGTH_LONG).show()
        }
    }
    fun changepass(username:String,newpass:String) {
        mFirebaseDatabaseInstances?.collection("Student")!!.document(username.toString()).get()
            .addOnSuccessListener { result ->
                val stud = result.toObject(Student::class.java)
                if (stud == null) {
                    Toast.makeText(this, "UNEXPECTED ERROR", Toast.LENGTH_LONG).show()
                } else {

                    var u = Student(stud.name,stud.usn,stud.branch,stud.year, stud.sec,stud.email, stud.mobile, stud.address,newpass,stud.image,stud.label1,stud.label2)
                    mFirebaseDatabaseInstances?.collection("Student")?.document(username!!)?.set(u)
                    //Toast.makeText(this, "PASSWORD CHANGED SUCESSFULLY", Toast.LENGTH_LONG).show()

                    //alert box code

                    val dialogBuilder = android.app.AlertDialog.Builder(this)

                    // set message of alert dialog
                    dialogBuilder.setMessage("Password changed successfully")
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
    }
}