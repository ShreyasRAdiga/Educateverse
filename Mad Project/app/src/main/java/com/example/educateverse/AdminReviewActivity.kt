package com.example.educateverse

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminReviewActivity : AppCompatActivity() {
    var us=""
    var n=""
    var b=""
    var yr=""
    var s=""
    var e=""
    var m=""
    var a=""
    var pas=""
    var url=""

    private var mAuth: FirebaseAuth?=null
    private var mFirebaseDatabaseInstances: FirebaseFirestore?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_review)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        mAuth= FirebaseAuth.getInstance()
        mFirebaseDatabaseInstances= FirebaseFirestore.getInstance()
        supportActionBar?.title="Review Student Requests"
        loadData()
    }
    @SuppressLint("ResourceType")
    fun loadData(){
        var i= intent
        var u= i.getStringExtra("usn")
        if (u != null) {
            us=u
        }
        try{
            mFirebaseDatabaseInstances?.collection("StudentReq")!!.document(u.toString()).get().addOnSuccessListener {
                    result ->

                var uu=result.toObject(Student::class.java)
                if(uu==null){
                    //Toast.makeText(this, "ERROR: CAN'T LOAD DATA", Toast.LENGTH_LONG).show()

                    //alert box code

                    val dialogBuilder = AlertDialog.Builder(this)

                    // set message of alert dialog
                    dialogBuilder.setMessage("Can't Load The Data")
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
                    var name=(findViewById<View>(R.id.name) as TextView)
                    var usn=(findViewById<View>(R.id.usn)as TextView)
                    var branch=(findViewById<View>(R.id.branch)as Spinner)
                    var year=(findViewById<View>(R.id.year)as Spinner)
                    var section=(findViewById<View>(R.id.section)as Spinner)
                    var email=(findViewById<View>(R.id.email)as TextView)
                    var mobile=(findViewById<View>(R.id.mobile)as TextView)
                    var address=(findViewById<View>(R.id.address)as TextView)

                    name.text=uu.name
                    n=uu.name

                    usn.text=uu.usn
                    us=uu.usn

                    var branchs: Array<out String> =resources.getStringArray(R.array.Branch)
                    var ctr=0
                    for (br in branchs ){
                        ctr++
                        if(br.equals(uu.branch))
                            break;
                    }
                    branch.setSelection(ctr-1)
                    b=uu.branch

                    var years=resources.getStringArray(R.array.semester)
                    ctr=0
                    for(year in years){
                        ctr++
                        if(year.equals(uu.year))
                            break;
                    }
                    year.setSelection(ctr-1)
                    yr=uu.year

                    var sections=resources.getStringArray(R.array.section)
                    ctr=0
                    for(sect in sections ){
                        ctr++
                        if(sect.equals(uu.sec))
                            break;
                    }
                    section.setSelection(ctr-1)
                    s=uu.sec

                    email.text=uu.email
                    e=uu.email

                    mobile.text=uu.mobile
                    m=uu.mobile

                    address.text=uu.address
                    a=uu.address

                    pas=uu.password
                    url=uu.image

                }
            }
        }
        catch(e:Exception)
        {
           // Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()

            //alert box code

            val dialogBuilder = AlertDialog.Builder(this)

            // set message of alert dialog
            dialogBuilder.setMessage("Error")
                // positive button text and action
                .setPositiveButton("Ok", DialogInterface.OnClickListener {
                        dialog, id ->
                })


            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Alert")
            // show alert dialog
            alert.show()

            //alert box code upto here

        }
    }

    fun accept(v: View?) {
        val docRef = mFirebaseDatabaseInstances?.collection("Student")?.document(us!!)
        docRef?.get()?.addOnSuccessListener { documentSnapshot ->
            val user = documentSnapshot.toObject(Student::class.java) as Student?
            if (user != null)//User Already exists
            {
                //Toast.makeText(this, "ERROR: STUDENT RECORD EXIST", Toast.LENGTH_LONG).show()

                //alert box code

                val dialogBuilder = AlertDialog.Builder(this)

                // set message of alert dialog
                dialogBuilder.setMessage("Student Record Exists")
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
                var lab1 = label1(yr, s)
                var lab2=""
                var u = Student(n, us, b, yr, s, e, m, a, pas,url, lab1,lab2)
                mFirebaseDatabaseInstances?.collection("Student")?.document(us!!)?.set(u)
                //Toast.makeText(this, "SUCCESS: STUDENT RECORD ADDED", Toast.LENGTH_LONG).show()

                //alert box code

                val dialogBuilder = AlertDialog.Builder(this)

                // set message of alert dialog
                dialogBuilder.setMessage("Student Record Added")
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

                del()
            }
        }

    }
    fun decline(v: View?){
        mFirebaseDatabaseInstances?.collection("StudentReq")?.document(us)
            ?.delete()
            ?.addOnSuccessListener {
                //Removed ( after flower
                    //Toast.makeText(this, "SUCCESS: STUDENT REQUEST DECLINED", Toast.LENGTH_LONG).show()

                var i = Intent(applicationContext, StudentRequest::class.java)
                //i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)


                //alert box code

                val dialogBuilder = AlertDialog.Builder(this)

                // set message of alert dialog
                dialogBuilder.setMessage("Student Request Declined")
                    // positive button text and action
                    .setPositiveButton("Ok", DialogInterface.OnClickListener {
                            dialog, id ->startActivity(i)
                    })


                // create dialog box
                val alert = dialogBuilder.create()
                // set title for alert dialog box
                alert.setTitle("Success")
                // show alert dialog
                alert.show()

                //alert box code upto here

                // add ) before flower
            }
            ?.addOnFailureListener {
                //Toast.makeText(this, "ERROR: CAN'T SERVE THE REQUEST", Toast.LENGTH_LONG).show()

                //alert box code

                val dialogBuilder = AlertDialog.Builder(this)

                // set message of alert dialog
                dialogBuilder.setMessage("Can't Save The Request")
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
        // var i=Intent(applicationContext,StudentRequest::class.java)
        // startActivity(i)

    }
    fun label1(year:String,sec:String):String{
        return "$year$sec"
    }
    fun del(){
        mFirebaseDatabaseInstances?.collection("StudentReq")?.document(us)
            ?.delete()
            ?.addOnSuccessListener { }
            ?.addOnFailureListener { }
        //var i=Intent(applicationContext,StudentRequest::class.java)
        //startActivity(i)

    }
}
