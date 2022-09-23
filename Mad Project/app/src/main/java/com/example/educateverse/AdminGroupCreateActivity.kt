package com.example.educateverse

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.net.URL

class AdminGroupCreateActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var mFirebaseDatabaseInstances: FirebaseFirestore? = null
    lateinit var facrv: RecyclerView
    lateinit var studrv: RecyclerView
    var username=""

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_group_create)
        //seletedpeople.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        supportActionBar?.title = "Create Group"
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabaseInstances = FirebaseFirestore.getInstance()
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        facrv = findViewById(R.id.rvfac) as RecyclerView
        studrv=findViewById(R.id.rvstud)as RecyclerView
        var i=intent
        username = i.getStringExtra("username").toString()

    }


    fun searchf(v: View?) {
        var fbranch = (findViewById<View>(R.id.branchf) as Spinner).selectedItem.toString()
        val query: Query = FirebaseFirestore.getInstance().collection("Faculty").whereEqualTo("branch", fbranch)
        val faculties: FirestoreRecyclerOptions<Faculty?> = FirestoreRecyclerOptions.Builder<Faculty>().setQuery(query, Faculty::class.java).build()
        val adapter: FirestoreRecyclerAdapter<*, *> = object : FirestoreRecyclerAdapter<Faculty?, RecyclerView.ViewHolder?>(faculties) {

            override fun onCreateViewHolder(group: ViewGroup, i: Int): FacHolder {
                val view: View = LayoutInflater.from(group.context)
                    .inflate(R.layout.group_create_rows, group, false)
                return FacHolder(view)
            }

            override fun onBindViewHolder(
                holder: RecyclerView.ViewHolder,
                position: Int,
                fac: Faculty,
            ) {


                (holder.itemView.findViewById<View>(R.id.new_msg_text) as TextView).text = "${fac.honorific} ${fac.name}"
                if (fac.image != "") {
                    var url = URL(fac.image)
                    var imageView =
                        (holder.itemView.findViewById<View>(R.id.new_msg_img) as ImageView)
                    Glide.with(this@AdminGroupCreateActivity).load(fac.image)
                        .placeholder(R.drawable.person).error(R.drawable.person)
                        .override(600, 600).into(imageView);
                }
                (holder.itemView.findViewById<View>(R.id.checkBoxf)as CheckBox).setOnCheckedChangeListener{ buttonView, isChecked->
                    if(isChecked){
                        val docRef = mFirebaseDatabaseInstances?.collection("GroupingFac")?.document(fac.FID!!)
                        docRef?.get()?.addOnSuccessListener { documentSnapshot ->
                            val user = documentSnapshot.toObject(Faculty::class.java) as Faculty?
                            if (user != null)//User Already exists
                            {
                            }
                            else {
                                mFirebaseDatabaseInstances?.collection("GroupingFac")?.document(fac.FID!!)?.set(fac)
                            }
                        }
                    }
                    else if(!isChecked){
                        mFirebaseDatabaseInstances?.collection("GroupingFac")?.document(fac.FID)
                            ?.delete()
                    }
                }


            }
        }
        facrv.layoutManager = LinearLayoutManager(this)
        facrv.adapter = adapter
        adapter.startListening()
    }


    fun searchs(v: View?) {
        var sbranch = (findViewById<View>(R.id.branchs) as Spinner).selectedItem.toString()
        var ssem = (findViewById<View>(R.id.semesters) as Spinner).selectedItem.toString()
        var sec = (findViewById<View>(R.id.sections) as Spinner).selectedItem.toString()
        val query: Query = FirebaseFirestore.getInstance().collection("Student").whereEqualTo("branch", sbranch).whereEqualTo("year",ssem).whereEqualTo("sec",sec)
        val students: FirestoreRecyclerOptions<Student?> = FirestoreRecyclerOptions.Builder<Student>().setQuery(query, Student::class.java).build()
        val adapter: FirestoreRecyclerAdapter<*, *> = object : FirestoreRecyclerAdapter<Student?, RecyclerView.ViewHolder?>(students) {

            override fun onCreateViewHolder(group: ViewGroup, i: Int): StudHolder {
                val view: View = LayoutInflater.from(group.context)
                    .inflate(R.layout.group_create_rows, group, false)
                return StudHolder(view)
            }

            override fun onBindViewHolder(
                holder: RecyclerView.ViewHolder,
                position: Int,
                stud: Student,
            ) {

                (holder.itemView.findViewById<View>(R.id.new_msg_text) as TextView).setText("${stud.name} (${stud.usn})")
                if (stud.image != "") {
                    var url = URL(stud.image)
                    var imageView =
                        (holder.itemView.findViewById<View>(R.id.new_msg_img) as ImageView)
                    Glide.with(this@AdminGroupCreateActivity).load(stud.image)
                        .placeholder(R.drawable.person).error(R.drawable.person)
                        .override(600, 600).into(imageView);

                }
                val docRef = mFirebaseDatabaseInstances?.collection("GroupingStud")
                    ?.document(stud.usn!!)
                docRef?.get()?.addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.toObject(Faculty::class.java) as Student?
                    if (user != null)//User Already exists
                    {
                        (holder.itemView.findViewById<View>(R.id.checkBoxf) as CheckBox).isChecked =
                            true
                    }
                }

                (holder.itemView.findViewById<View>(R.id.checkBoxf)as CheckBox).setOnCheckedChangeListener{ buttonView, isChecked->
                    if(isChecked){
                        val docRef = mFirebaseDatabaseInstances?.collection("GroupingStud")?.document(stud.usn!!)
                        docRef?.get()?.addOnSuccessListener { documentSnapshot ->
                            val user = documentSnapshot.toObject(Faculty::class.java) as Student?
                            if (user != null)//User Already exists
                            {
                            }
                            else {
                                mFirebaseDatabaseInstances?.collection("GroupingStud")?.document(stud.usn!!)?.set(stud)
                            }
                        }
                    }
                    else if(!isChecked){
                        mFirebaseDatabaseInstances?.collection("GroupingStud")?.document(stud.usn)?.delete()
                    }
                }

            }
        }
        studrv.layoutManager = LinearLayoutManager(this)
        studrv.adapter = adapter
        adapter.startListening()
    }
    fun continuenext(v: View?){
        var intent= Intent(applicationContext,AdminFinilizeGroupActivity::class.java)
        intent.putExtra("username",username)
        startActivity(intent)

    }
}

class FacHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val facname: TextView

    init {
        facname = itemView.findViewById(R.id.new_msg_text)
    }
}
class StudHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val studname: TextView

    init {
        studname = itemView.findViewById(R.id.new_msg_text)
    }
}