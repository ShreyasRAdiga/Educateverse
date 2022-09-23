package com.example.educateverse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class StudentRequest : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var mFirebaseDatabaseInstances: FirebaseFirestore? = null
    lateinit var cv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_student_request)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabaseInstances = FirebaseFirestore.getInstance()
        cv = findViewById<View>(R.id.rcView) as RecyclerView
        supportActionBar?.title="Student Requests"
        // loadData()


    }

    fun search(v: View?) {
        var branchSelected=(findViewById<View>(R.id.branch)as Spinner).selectedItem.toString()
        val query: Query = FirebaseFirestore.getInstance()
            .collection("StudentReq").whereEqualTo("branch",branchSelected)
        val options: FirestoreRecyclerOptions<Student?> = FirestoreRecyclerOptions.Builder<Student>().setQuery(query, Student::class.java).build()
        val adapter: FirestoreRecyclerAdapter<*, *> = object : FirestoreRecyclerAdapter<Student?, RecyclerView.ViewHolder?>(options) {

            override fun onCreateViewHolder(group: ViewGroup, i: Int): StudentNameHolder {
                // Using a custom layout called R.layout.message for each item, we create a new instance of the viewholder
                val view: View = LayoutInflater.from(group.context)
                    .inflate(R.layout.list_item, group, false)
                return StudentNameHolder(view)
            }


            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: Student) {

                (holder.itemView.findViewById<View>(R.id.rctxtname) as TextView).setText(
                    model.usn)
                (holder.itemView.findViewById<View>(R.id.rcBtn) as Button).setOnClickListener {
                    var i = Intent(applicationContext, AdminReviewActivity::class.java)
                    i.putExtra("usn", model.usn)

                    startActivity(i)
                }

            }


        }

        cv.layoutManager = LinearLayoutManager(this)
        cv.adapter = adapter
        adapter.startListening()
    }
}


class StudentNameHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val rcView: TextView =itemView.findViewById(R.id.rctxtname)
}


