package com.example.educateverse

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.user_row_new_msg.view.*
import java.lang.Exception
import java.net.URL


class NewMessageActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var mFirebaseDatabaseInstances: FirebaseFirestore? = null
    lateinit var cv: RecyclerView
    var username = ""
    var name = ""
    var studUsername = ""
    var toID = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faculty_new_message)
        supportActionBar?.title = "Find Student"
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabaseInstances = FirebaseFirestore.getInstance()
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        cv = findViewById<View>(R.id.rv_new_message) as RecyclerView

        //loadStudents()
        //loadData()

    }

    fun search(v: View?) {
        var i = intent
        var FBr = i.getStringExtra("FBr")
        username = i.getStringExtra("username").toString()
        name = i.getStringExtra("name").toString()
        var year = (findViewById<View>(R.id.years) as Spinner).selectedItem.toString()
        var section = (findViewById<View>(R.id.sections) as Spinner).selectedItem.toString()

        val docRef = mFirebaseDatabaseInstances?.collection("Student")?.whereEqualTo("branch", FBr)
            ?.whereEqualTo("year", year)
            ?.whereEqualTo("sec", section)
        if (docRef != null) {

            docRef.addSnapshotListener { snapshot, e ->
                run {

                    val adapter = GroupieAdapter()
                    cv.adapter = adapter
                    for (dc in snapshot!!.documentChanges) {
                        adapter.add(StudentHolder(dc))

                    }

                }
            }

        }
    }


    fun loadData() {
        var i = intent
        var FBr = i.getStringExtra("FBr")
        username = i.getStringExtra("username").toString()
        name = i.getStringExtra("name").toString()


        val query: Query =
            FirebaseFirestore.getInstance().collection("Student").whereEqualTo("branch", FBr)

        val options: FirestoreRecyclerOptions<Student?> =
            FirestoreRecyclerOptions.Builder<Student>().setQuery(query, Student::class.java).build()
        val adapter: FirestoreRecyclerAdapter<*, *> =
            object : FirestoreRecyclerAdapter<Student?, RecyclerView.ViewHolder?>(options) {


                override fun onCreateViewHolder(group: ViewGroup, i: Int): StudentNameHolderNewMsg {
                    // Using a custom layout called R.layout.message for each item, we create a new instance of the viewholder
                    val view: View = LayoutInflater.from(group.context)
                        .inflate(R.layout.user_row_new_msg, group, false)
                    return StudentNameHolderNewMsg(view)
                }


                override fun onBindViewHolder(
                    holder: RecyclerView.ViewHolder,
                    position: Int,
                    model: Student
                ) {
                    (holder.itemView.findViewById<View>(R.id.new_msg_text) as TextView).setText("${model.name} (${model.usn})")

                    studUsername = "${model.name} (${model.usn})"
                    toID = model.usn
                    if (model.image != "") {
                        var url = URL(model.image)
                        var imageView =
                            (holder.itemView.findViewById<View>(R.id.new_msg_img) as ImageView)
                        Glide.with(this@NewMessageActivity).load(model.image)
                            .placeholder(R.drawable.person).error(R.drawable.person)
                            .override(80, 80).into(imageView)
                    }

                }

            }


        cv.layoutManager = LinearLayoutManager(this)
        cv.adapter = adapter
        adapter.startListening()


    }

    fun chat(v: View?) {
        val pattern = Regex("\\s+")
        if (v != null) {
            toID = v.new_msg_text.text.toString()
        }
        val ans: List<String> = pattern.split(toID)
        toID = ans.get(0)

        mFirebaseDatabaseInstances?.collection("Student")!!.document(toID.toString()).get()
            .addOnSuccessListener { result ->
                val stud = result.toObject(Student::class.java)
                if (stud == null) {
                    Toast.makeText(this, "ERROR : CAN'T LOAD DATA", Toast.LENGTH_LONG).show()

                    //alert box code

                    val dialogBuilder = AlertDialog.Builder(this)

                    // set message of alert dialog
                    dialogBuilder.setMessage("Can't load the data.")
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
                    studUsername = stud.name
                }
                val intent = Intent(applicationContext, ChatActivity::class.java)
                Log.d("Stud", v.toString())
                intent.putExtra("toname", studUsername)
                intent.putExtra("toID", toID)
                intent.putExtra("fromID", username)
                intent.putExtra("fromname", name)
                startActivity(intent)
                finish()

            }


    }
}

class StudentNameHolderNewMsg(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val rcIV: ImageView = itemView.findViewById(R.id.new_msg_img)
    val rcText: TextView = itemView.findViewById(R.id.new_msg_text)
}

class StudentHolder(val dc: DocumentChange) : Item<GroupieViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.user_row_new_msg
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        var name = dc.document.get("name").toString()
        var usn = dc.document.get("usn").toString()

        viewHolder.itemView.new_msg_text.text = "$usn ($name)"
        try {
            Picasso.get().load(dc.document.get("image").toString()).into(viewHolder.itemView.new_msg_img)
        } catch (e: Exception) {
            viewHolder.itemView.new_msg_img.setBackgroundResource(R.drawable.person)
        }

    }
}
