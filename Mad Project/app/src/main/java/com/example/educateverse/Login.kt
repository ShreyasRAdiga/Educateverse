package com.example.educateverse

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class Login : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var mFirebaseDatabaseInstances: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabaseInstances = FirebaseFirestore.getInstance()
    }


    fun login(v: View?) {
        val s = findViewById<View>(R.id.user) as Spinner
        val user = s.selectedItem.toString()
        var username = findViewById<View>(R.id.username) as EditText
        var password = findViewById<View>(R.id.pass) as EditText
        if (user.equals("STUDENT")) {
            var usn: String = username.text.toString().uppercase(Locale.getDefault())
            try {
                mFirebaseDatabaseInstances?.collection("Student")!!.document(usn).get()
                    .addOnSuccessListener { result ->
                        val uu = result.toObject(Student::class.java) as Student?
                        if (uu == null) {
                            //Toast.makeText(this, "ERROR: STUDENT RECORD DOESN'T EXIST",Toast.LENGTH_LONG).show()

                            //alert box code

                            val dialogBuilder = AlertDialog.Builder(this)

                            // set message of alert dialog
                            dialogBuilder.setMessage("Student record doesn't exists.")
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



                        }
                        else {
                            if (uu.password.equals(password.text.toString())) {

                                // Toast.makeText(this, "SUCCESS : LOGIN SUCCESSFUL", Toast.LENGTH_LONG).show()
                                var i = Intent(applicationContext, StudentHomeActivity::class.java)
                                i.putExtra("usn", usn)
                                i.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)


                                //alert box code

                                val dialogBuilder = AlertDialog.Builder(this)

                                // set message of alert dialog
                                dialogBuilder.setMessage("Welcome Back!")
                                    // positive button text and action
                                    .setPositiveButton("Ok", DialogInterface.OnClickListener {
                                            dialog, id ->

                                        startActivity(i)
                                    })


                                // create dialog box
                                val alert = dialogBuilder.create()
                                // set title for alert dialog box
                                alert.setTitle("Educateverse")
                                // show alert dialog
                                alert.show()

                                //alert box code upto here

                            } else {
                                //Toast.makeText(this, "ERROR: INVALID CREDENTIALS", Toast.LENGTH_LONG).show()
                                val intent = Intent(this, Error::class.java)
                                startActivity(intent)
                            }

                        }
                    }

                    .addOnFailureListener { exception ->
                        //Toast.makeText(this, "ERROR : STUDENT RECORD DOESN'T EXIST", Toast.LENGTH_LONG).show()

                        //alert box code

                        val dialogBuilder = AlertDialog.Builder(this)

                        // set message of alert dialog
                        dialogBuilder.setMessage("Student record doesn't exist.")
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

                    }
            } catch (e: Exception) {
                Toast.makeText(this, "ERROR: STUDENT RECORD DOESN'T EXIST", Toast.LENGTH_LONG).show()

                //alert box code

                val dialogBuilder = AlertDialog.Builder(this)

                // set message of alert dialog
                dialogBuilder.setMessage("Student record doesn't exist.")
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

            }

        } else if (user.equals("ADMIN")) {
            try {
                mFirebaseDatabaseInstances?.collection("Admin")!!
                    .document(username.text.toString().uppercase(Locale.getDefault())).get()
                    .addOnSuccessListener { result ->
                        val uu = result.toObject(Student::class.java) as Student?
                        if (uu == null) {
                           // Toast.makeText(this, "ERROR: ADMIN DOESN'T EXIST", Toast.LENGTH_LONG).show()

                            //alert box code

                            val dialogBuilder = AlertDialog.Builder(this)

                            // set message of alert dialog
                            dialogBuilder.setMessage("ADMIN DOESN'T EXIST")
                                // positive button text and action
                                .setPositiveButton("Ok", DialogInterface.OnClickListener {
                                        dialog, id ->
                                })


                            // create dialog box
                            val alert = dialogBuilder.create()
                            // set title for alert dialog box
                            alert.setTitle("Record ERROR")
                            // show alert dialog
                            alert.show()

                            //alert box code upto here


                        } else {
                            if (uu.password.equals(password.text.toString())) {

                                var i = Intent(applicationContext, AdminHomeActivity::class.java)
                                i.putExtra("username", username.text.toString().uppercase())
                                i.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)

                                //alert box code

                                val dialogBuilder = AlertDialog.Builder(this)

                                // set message of alert dialog
                                dialogBuilder.setMessage("Welcome Back!")
                                    // positive button text and action
                                    .setPositiveButton("Ok", DialogInterface.OnClickListener {
                                            dialog, id ->startActivity(i)
                                    })


                                // create dialog box
                                val alert = dialogBuilder.create()
                                // set title for alert dialog box
                                alert.setTitle("Educateverse")
                                // show alert dialog
                                alert.show()

                                //alert box code upto here


                            } else {
                                //Toast.makeText(this, "ERROR: INVALID CREDENTIALS", Toast.LENGTH_LONG).show()
                                val intent = Intent(this, Error::class.java)
                                this.startActivity(intent)
                            }

                        }
                    }

                    .addOnFailureListener { exception ->
                        //Toast.makeText(this, "ERROR : ADMIN RECORD DOESN'T EXIST", Toast.LENGTH_LONG).show()

                        //alert box code

                        val dialogBuilder = AlertDialog.Builder(this)

                        // set message of alert dialog
                        dialogBuilder.setMessage("Admin record doesn't exist.")
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

                    }
            }

            catch(e: Exception) {
                //Toast.makeText(this, "ERROR: ADMIN DOESN'T EXIST", Toast.LENGTH_LONG).show()

                //alert box code

                val dialogBuilder = AlertDialog.Builder(this)

                // set message of alert dialog
                dialogBuilder.setMessage("Admin doesn't exist.")
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


            }

            /*if ((username.text.toString()).equals("Admin") && (password.text.toString()).equals("pas")) {
                var i = Intent(applicationContext, AdminHomeActivity::class.java)
                i.putExtra("user", "Admin")
                i.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)
            }
            else {
                Toast.makeText(this, "ERROR: INVALID CREDENTIALS", Toast.LENGTH_LONG).show()
            }*/
        }

        else if(user.equals("FACULTY")) {
            var username: String = username.text.toString()
            try {
                mFirebaseDatabaseInstances?.collection("Faculty")!!.document(username).get()
                    .addOnSuccessListener { result ->
                        val uu = result.toObject(Student::class.java) as Student?
                        if (uu == null) {
                            //Toast.makeText(this, "ERROR: FACULTY RECORD DOESN'T EXIST", Toast.LENGTH_LONG).show()

                            //alert box code

                            val dialogBuilder = AlertDialog.Builder(this)

                            // set message of alert dialog
                            dialogBuilder.setMessage("Faculty Record Doesn't Exists")
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

                        }
                        else {
                            if (uu.password.equals(password.text.toString())) {


                                var i = Intent(applicationContext, FacultyHomeActivity::class.java)
                                i.putExtra("username", username)
                                i.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                             // startActivity(i)
                                //Toast.makeText(this, "LOGIN SUCCESSFUL", Toast.LENGTH_LONG).show()

                                //alert box code

                                val dialogBuilder = AlertDialog.Builder(this)

                                // set message of alert dialog
                                dialogBuilder.setMessage("Welcome Back!")
                                    // positive button text and action
                                    .setPositiveButton("Ok", DialogInterface.OnClickListener {
                                            dialog, id ->

                                        startActivity(i) })



                                // create dialog box
                                val alert = dialogBuilder.create()
                                // set title for alert dialog box
                                alert.setTitle("Educateverse")
                                // show alert dialog
                                alert.show()


                                //alert box code upto here


                            }
                            else{
                                //Toast.makeText(this, "ERROR: INVALID CREDENTIALS", Toast.LENGTH_LONG).show()
                                val intent = Intent(this, Error::class.java)
                                this.startActivity(intent)
                            }

                        }
                    }

                    .addOnFailureListener { exception ->
                        //Toast.makeText(this, "ERROR : FACULTY RECORD DOESN'T EXIST", Toast.LENGTH_LONG).show()

                        //alert box code

                        val dialogBuilder = AlertDialog.Builder(this)

                        // set message of alert dialog
                        dialogBuilder.setMessage("Faculty record doesn't exist.")
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


                    }
            }
            catch (e: Exception) {
                //Toast.makeText(this, "ERROR: FACULTY RECORD DOESN'T EXIST", Toast.LENGTH_LONG).show()

                //alert box code

                val dialogBuilder = AlertDialog.Builder(this)

                // set message of alert dialog
                dialogBuilder.setMessage("Faculty record doesn't exist.")
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



            }
        }


    }

    fun register(v: View?) {
        var i = Intent(applicationContext, Registration::class.java)
        startActivity(i)
    }
}
